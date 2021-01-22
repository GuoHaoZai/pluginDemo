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

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import guohao.code.generator.constant.MethodPrefixConstants;
import guohao.code.generator.constant.MenuNameConstants;
import guohao.code.generator.utils.PsiClassUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GenerateWithBuilderAction extends PsiElementBaseIntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiClass localVariableContainingClass = PsiClassUtils.buildFrom(element);
        for (PsiMethod method : localVariableContainingClass.getMethods()) {
            if (method.getName().equals(MethodPrefixConstants.BUILDER)) {
                PsiType returnType = method.getReturnType();
                PsiClass psiClass = PsiTypesUtil.getPsiClass(returnType);
                StringBuilder builder = new StringBuilder(localVariableContainingClass.getQualifiedName() + ".builder()");
                for (PsiMethod psiClassMethod : psiClass.getMethods()) {
                    if (!psiClassMethod.isConstructor() && !psiClassMethod.getName().equals("toString")
                            && !psiClassMethod.getName().equals("build")) {
                        builder.append(".").append(psiClassMethod.getName()).append("()");
                    }
                }
                builder.append(".build();");

                // insert into the element.
                Document document = PsiDocumentManager.getInstance(project).getDocument(element.getContainingFile());

                WriteCommandAction.runWriteCommandAction(project, () -> {
                    document.insertString(element.getTextRange().getEndOffset() + 2, builder.toString());
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                });

            }
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiClass localVarialbeContainingClass = PsiClassUtils.buildFrom(element);
        if (localVarialbeContainingClass == null) {
            return false;
        }
        PsiMethod[] methods = localVarialbeContainingClass.getMethods();
        for (PsiMethod method : methods) {
            if (method.getName().equals(MethodPrefixConstants.BUILDER) && method.hasModifierProperty(PsiModifier.STATIC)) {
                return true;
            }
        }
        return false;
    }


    @NotNull
    @Override
    public String getText() {
        return MenuNameConstants.GENERATOR;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return MenuNameConstants.GENERATE_BUILDER_METHOD;
    }
}
