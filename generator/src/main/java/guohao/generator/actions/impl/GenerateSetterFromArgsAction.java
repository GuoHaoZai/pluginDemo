/*
 *  Copyright (c) 2017-2019, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

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
import guohao.generator.actions.AbstractGenerateSetterAction;
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
public class GenerateSetterFromArgsAction extends AbstractGenerateSetterAction {

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
                    return new CustomClassInfo("", "", instance, Source.OTHER);
                })
                .findFirst();
    }

    //endregion
}
