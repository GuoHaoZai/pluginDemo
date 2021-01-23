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

package guohao.code.generator.actions;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.util.IncorrectOperationException;
import guohao.code.generator.constant.MethodPrefixConstants;
import guohao.code.generator.plugin.BundleManager;
import guohao.code.utils.PsiClassUtils;
import guohao.code.utils.PsiDocumentUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GenerateBuilderAction extends PsiElementBaseIntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiClass publicClass = PsiClassUtils.buildFrom(element);
        for (PsiMethod method : publicClass.getMethods()) {
            if (method.getName().equals(MethodPrefixConstants.BUILDER)) {

                PsiType returnType = method.getReturnType();
                PsiClass buildClass = PsiTypesUtil.getPsiClass(returnType);

                String text = buildText(publicClass, buildClass);

                // insert into the element.
                Document document = PsiDocumentUtils.getDocument(element);
                document.insertString(element.getTextRange().getEndOffset() + 2, text);
                PsiDocumentUtils.commitAndSaveDocument(element, document);
            }
        }
    }

    private String buildText(PsiClass publicClass, PsiClass buildClass) {
        if (!Objects.equals(publicClass.getText(), buildClass.getText())) {
            StringBuilder builder = new StringBuilder(publicClass.getQualifiedName() + ".builder()");
            for (PsiMethod psiClassMethod : buildClass.getMethods()) {

                if (!psiClassMethod.isConstructor()
                        && !psiClassMethod.getName().equals("toString")
                        && !psiClassMethod.getName().equals(MethodPrefixConstants.BUILD)) {

                    builder.append(".").append(psiClassMethod.getName()).append("()");
                }
            }
            return builder.append(".").append(MethodPrefixConstants.BUILD + "();").toString();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiClass psiClass = PsiClassUtils.buildFrom(element);
        if (psiClass == null) {
            return false;
        }
        return PsiClassUtils.hasBuilderMethod(psiClass);
    }


    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.builder.method");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return BundleManager.getProjectBundle("plugin.generator.settings.title");
    }
}
