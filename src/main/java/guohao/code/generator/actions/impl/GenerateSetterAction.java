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

package guohao.code.generator.actions.impl;

import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import guohao.code.generator.actions.AbstractGenerateSetterAction;
import guohao.code.generator.meta.BasicClassInfo;
import guohao.code.generator.meta.ClassInfo;
import guohao.code.generator.plugin.BundleManager;
import guohao.code.generator.utils.ClassNameUtils;
import guohao.code.generator.utils.PsiMethodUtils;
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
public class GenerateSetterAction extends AbstractGenerateSetterAction {

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
    protected List<ClassInfo> parseMethod(PsiLocalVariable localVariable, PsiMethod setterMethod) {
        return Optional.of(setterMethod)
                .map(PsiMethodUtils::getPsiParameters).orElse(Collections.emptyList())
                .stream()
                .map(this::parseParameter)
                .collect(Collectors.toList());
    }

    /**
     * 根据每一个参数的类型
     * @param parameter
     * @return
     */
    @NotNull
    private ClassInfo parseParameter(PsiParameter parameter) {
        return Optional.ofNullable(parameter)
                .map(PsiParameter::getType)
                .map(PsiType::getCanonicalText)
                .map(ClassNameUtils::removeGeneric)
                .map(BasicClassInfo::get).orElse(Collections.emptyList())
                .stream()
                .filter(basicClassInfo -> basicClassInfo.getSource().getCanUse().test(parameter))
                .min(Comparator.comparingInt(cl -> cl.getSource().getIndex()))
                .orElse(BasicClassInfo.NULL);
    }
    //endregion
}
