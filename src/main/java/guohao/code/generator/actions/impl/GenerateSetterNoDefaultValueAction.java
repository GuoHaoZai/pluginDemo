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
import guohao.code.generator.actions.AbstractGenerateSetterAction;
import guohao.code.generator.meta.ClassInfo;
import guohao.code.generator.meta.CustomClassInfo;
import guohao.code.generator.meta.Source;
import guohao.code.generator.plugin.BundleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GenerateSetterNoDefaultValueAction extends AbstractGenerateSetterAction {

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.setter.value.null");
    }

    //region 生成空参数
    @Override
    protected List<ClassInfo> parseMethod(PsiLocalVariable localVariable, PsiMethod setterMethod) {
        return Collections.singletonList(new CustomClassInfo("", "", "", Source.OTHER));
    }
    //endregion
}
