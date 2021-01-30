package guohao.generator.actions.impl;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import guohao.generator.BundleManager;
import guohao.common.MethodPrefixConstants;
import guohao.common.PsiClassUtils;
import guohao.common.PsiMethodUtils;
import guohao.generator.actions.AbstractGeneratorSetterAction;
import guohao.generator.meta.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
    protected List<ClassInfo> parseMethod(PsiLocalVariable localVariable, PsiMethod setterMethod){
        List<PsiParameter> methodParameters = Optional.of(localVariable)
                .map(variable -> PsiTreeUtil.getParentOfType(variable, PsiMethod.class))
                .map(PsiMethodUtils::getPsiParameters).orElse(Collections.emptyList());

        return Optional.of(setterMethod)
                .map(PsiMethodUtils::getPsiParameters).orElse(Collections.emptyList())
                .stream()
                .map(parameter->parseParameters(methodParameters, setterMethod))
                .collect(Collectors.toList());
    }

    private ClassInfo parseParameters(List<PsiParameter> methodParameters, PsiMethod setterMethod) {
        for (PsiParameter methodParameter : methodParameters) {
            Optional<ClassInfo> classInfoOpt = parseParameter(setterMethod, methodParameter);
            if (classInfoOpt.isPresent()) {
                return classInfoOpt.get();
            }
        }
        return BasicClassInfo.NULL;
    }

    private Optional<ClassInfo> parseParameter(PsiMethod setterMethod, PsiParameter methodParameter) {
        String resultName = setterMethod.getName().replaceFirst(MethodPrefixConstants.SET, MethodPrefixConstants.GET);
        return Optional.of(methodParameter)
                .map(PsiParameter::getType)
                .map(PsiTypesUtil::getPsiClass)
                .map(PsiClassUtils::extractGetMethods)
                .stream()
                .flatMap(Collection::stream)
                .filter(method -> method.getName().equals(resultName))
                .<ClassInfo>map(method -> {
                    // TODO 根据参数匹配
                    String instance = methodParameter.getName() + "." + method.getName() + "()";
                    return new CustomClassInfo("", "", instance, Library.OTHER);
                })
                .findFirst();
    }

    //endregion
}