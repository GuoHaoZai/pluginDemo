package guohao.generator.actions.local.setter;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import guohao.common.MethodPrefixConstants;
import guohao.common.PsiClassUtils;
import guohao.common.PsiMethodUtils;
import guohao.generator.BundleManager;
import guohao.generator.actions.local.setter.AbstractGeneratorSetterAction;
import guohao.generator.meta.BasicClassInfo;
import guohao.generator.meta.ClassInfo;
import guohao.generator.meta.CustomClassInfo;
import guohao.generator.meta.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GeneratorSetterFromArgsAction extends AbstractGeneratorSetterAction {

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.setter.value.args");
    }

    //region 从方法入参提取参数
    @Override
    protected List<ClassInfo> calculateNewSetterParamClassInfos(PsiLocalVariable localVariable, PsiMethod setterMethod) {
        List<PsiParameter> methodParameters = Optional.of(localVariable)
                .map(variable -> PsiTreeUtil.getParentOfType(variable, PsiMethod.class))
                .map(PsiMethodUtils::getPsiParameters)
                .orElse(Collections.emptyList());

        return PsiMethodUtils.getPsiParameters(setterMethod).stream()
                .map(parameter -> parseParameters(methodParameters, setterMethod))
                .collect(Collectors.toList());
    }

    private ClassInfo parseParameters(List<PsiParameter> methodParameters, PsiMethod setterMethod) {
        return methodParameters.stream()
                .map(methodParameter -> parseParameter(setterMethod, methodParameter))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(BasicClassInfo.NULL);
    }

    private Optional<ClassInfo> parseParameter(PsiMethod setterMethod, PsiParameter methodParameter) {
        String resultName = setterMethod.getName().replaceFirst(MethodPrefixConstants.SET, MethodPrefixConstants.GET);
        return PsiClassUtils.getDeclarationPsiClass(methodParameter)
                .map(PsiClassUtils::extractGetMethods)
                .flatMap(getters -> getters.stream()
                        .filter(getter -> getter.getName().equals(resultName))
                        .map(getter -> {
                            // TODO 根据参数匹配
                            String instance = methodParameter.getName() + "." + getter.getName() + "()";
                            return new CustomClassInfo("", "", instance, Library.OTHER);
                        })
                        .findFirst()
                );
    }

    //endregion
}
