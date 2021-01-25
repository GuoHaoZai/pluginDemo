package guohao.simulator;

import com.intellij.psi.*;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.psi.impl.source.PsiExtensibleClass;
import guohao.common.PsiToolUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 注解处理的入口类。当前类只是一个Simulator(模拟器)。实际上只是可以告诉IDEA编译器
 * 一些额外的信息。对编译之后生成的代码没有任何影响。
 *
 * @author guohao
 * @since 2021/1/23
 */
public class SimulatorArgumentProvider extends PsiAugmentProvider {

    private final SimulatorRegistry simulatorRegistry;

    private SimulatorArgumentProvider(){
        simulatorRegistry = SimulatorRegistry.getInstance();
    }


    @NotNull
    @Override
    protected Set<String> transformModifiers(@NotNull PsiModifierList modifierList, @NotNull final Set<String> modifiers) {
        return simulatorRegistry.getModifierSimulators().stream()
                .filter(processor -> processor.isSupported(modifierList))
                .map(processor -> processor.transformModifiers(modifierList, modifiers))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    protected @NotNull <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type) {
        return getAugments(element, type, null);
    }

    @Override
    protected @NotNull <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element,
                                                                      @NotNull Class<Psi> type,
                                                                      @Nullable String nameHint) {
        // 只处理PsiClass、PsiField、PsiMethod
        if ((type != PsiClass.class && type != PsiField.class && type != PsiMethod.class)) {
            return Collections.emptyList();
        }

        // 只处理PsiExtensibleClass
        if (!(element instanceof PsiExtensibleClass)) {
            return Collections.emptyList();
        }

        // 不处理接口或者注解类
        final PsiClass psiClass = (PsiClass) element;
        if (psiClass.isAnnotationType() || psiClass.isInterface()) {
            return Collections.emptyList();
        }
        // 如果不包含模拟器
        if (!PsiToolUtils.hasSimulatorLibrary(element)) {
            return Collections.emptyList();
        }

        return simulatorRegistry.getSimulators(type).stream()
                .filter(processor -> Objects.equals(type, processor.getSupportedElementClass()))
                .filter(processor -> processor.notNameHintIsEqualToSupportedAnnotation(nameHint))
                .map(processor -> processor.simulate(element, nameHint).stream()
                        .map(processResult -> (Psi) processResult)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
