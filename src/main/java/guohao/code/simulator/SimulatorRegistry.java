package guohao.code.simulator;

import com.google.common.collect.Sets;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import guohao.code.simulator.impl.UtilsModifierSimulator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link Simulator} 和{@link ModifierSimulator}的注册中心。
 *
 * @author guohao
 * @since 2021/1/23
 */
public final class SimulatorRegistry {

    private final Map<Class<? extends PsiElement>, Set<Simulator>> type2Simulators;
    private final Set<ModifierSimulator> modifierSimulators;

    private static class SimulatorRegistryHolder {
        private static final SimulatorRegistry INSTANCE = new SimulatorRegistry();
    }

    private SimulatorRegistry() {
        Set<Simulator> simulators = getSimulatorTable();

        modifierSimulators = getModifierSimulatorTable();

        type2Simulators = simulators.stream()
                .collect(Collectors.groupingBy(Simulator::getSupportedElementClass,
                                               HashMap::new,
                                               Collectors.mapping(Function.identity(), Collectors.toSet())));
    }

    //region 注册表。所有的插件都需要在这里注册


    @NotNull
    private static Set<Simulator> getSimulatorTable() {
        return Sets.<Simulator>newHashSet(
        ).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @NotNull
    private static Set<ModifierSimulator> getModifierSimulatorTable() {
        return Sets.<ModifierSimulator>newHashSet(
                ApplicationManager.getApplication().getService(UtilsModifierSimulator.class)
        ).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    //endregion

    public static SimulatorRegistry getInstance() {
        return SimulatorRegistryHolder.INSTANCE;
    }

    /**
     * 获取参数模拟器，能够修改PSI模型
     */
    @NotNull
    public Set<Simulator> getSimulators(Class<? extends PsiElement> type) {
        return type2Simulators.getOrDefault(type, Collections.emptySet());
    }

    /**
     * 获取修饰符模拟器，能够修改修饰符。
     */
    @NotNull
    public Set<ModifierSimulator> getModifierSimulators() {
        return Collections.unmodifiableSet(modifierSimulators);
    }
}
