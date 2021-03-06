package guohao.generator.actions.local.setter;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import guohao.common.ClassNameUtils;
import guohao.common.PsiMethodUtils;
import guohao.generator.BundleManager;
import guohao.generator.actions.local.setter.AbstractGeneratorSetterAction;
import guohao.generator.meta.BasicClassInfo;
import guohao.generator.meta.ClassInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GeneratorSetterAction extends AbstractGeneratorSetterAction {

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.setter.value.default");
    }

    //region 生成默认参数

    /**
     * 解析SETTER方法
     */
    @Override
    protected List<ClassInfo> calculateNewSetterParamClassInfos(PsiLocalVariable localVariable, PsiMethod setterMethod) {
        return Optional.of(setterMethod)
                .map(PsiMethodUtils::getPsiParameters)
                .map(psiParameters -> psiParameters.stream().map(this::parseParameter).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * 根据每一个参数的类型
     *
     * @param parameter
     * @return
     */
    @NotNull
    private ClassInfo parseParameter(PsiParameter parameter) {
        return Optional.ofNullable(parameter)
                .map(PsiParameter::getType)
                .map(PsiType::getCanonicalText)
                .map(ClassNameUtils::removeGeneric)
                .map(BasicClassInfo::get)
                .flatMap(classInfo -> classInfo.stream()
                        .filter(basicClassInfo -> basicClassInfo.getLibrary().getCanUse().test(parameter))
                        .min(Comparator.comparingInt(cl -> cl.getLibrary().getIndex()))
                )
                .orElse(BasicClassInfo.NULL);
    }
    //endregion
}
