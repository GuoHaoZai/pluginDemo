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

package com.generator.complexreturntype.handler.impl;

import com.generator.Parameters;
import com.generator.complexreturntype.handler.ComplexReturnTypeHandler;
import com.generator.complexreturntype.meta.InsertDto;
import com.generator.utils.PsiToolUtils;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class SetReturnTypeHandler implements ComplexReturnTypeHandler {

    @NotNull
    @Override
    public InsertDto handle(Parameters returnParamInfo, String splitText, PsiParameter[] parameters, boolean hasGuava) {
        InsertDto insertDto = new InsertDto();
        String returnVariableName = "";
        StringBuilder insertText = new StringBuilder();
        insertText.append(splitText);
        if (returnParamInfo.getParams().size() > 0) {
            String realName = returnParamInfo.getParams().get(0).getRealName();
            returnVariableName = PsiToolUtils.lowerStart(realName) + "Set";
            insertText.append("Set<").append(realName).append("> ").append(returnVariableName).append("=");
        } else {
            returnVariableName = "set";
            insertText.append("Set ").append(returnVariableName).append("=");
        }

        if (hasGuava) {
            insertText.append("Sets.newHashSet();");
            insertDto.setImportList(Sets.newHashSet("com.google.common.collect.Sets"));

        } else {
            insertText.append("new HashSet<>();");
        }

        insertText.append(splitText).append("return ").append(returnVariableName).append(";");

        insertDto.setAddedText(insertText.toString());
        return insertDto;
    }
}
