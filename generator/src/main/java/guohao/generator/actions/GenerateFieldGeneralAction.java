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

package guohao.generator.actions;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import guohao.common.PsiDocumentUtils;
import guohao.common.PsiToolUtils;
import guohao.generator.BundleManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * 用于在类的所有Field上添加字符串
 *
 * @author guohao
 * @since 2021/1/20
 */
public class GenerateFieldGeneralAction extends PsiElementBaseIntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String fieldString = Messages.showInputDialog("", "输入插入的字符串", Messages.getInformationIcon());
        if (StringUtils.isNotBlank(fieldString)) {
            Optional.ofNullable(PsiTreeUtil.getParentOfType(element, PsiClass.class))
                    .map(PsiClass::getFields)
                    .map(Arrays::asList)
                    .ifPresent(psiFields -> psiFields.forEach(psiField -> addFieldString(fieldString, psiField)));
        }
    }

    /**
     * 将字符串添加到field头
     *
     * @param addedString 被添加的字符串
     * @param psiField    field
     */
    private void addFieldString(String addedString, PsiField psiField) {
        Document document = PsiDocumentUtils.getDocument(psiField);
        TextRange textRange = psiField.getTextRange();
        String formatString = PsiToolUtils.calculateLineHeaderToElementString(psiField);
        document.insertString(textRange.getStartOffset(), addedString + "\n" + formatString);
        PsiDocumentUtils.commitAndSaveDocument(psiField, document);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return Optional.of(element)
                .map(psiElement -> PsiTreeUtil.getParentOfType(psiElement, PsiClass.class))
                .map(PsiClass::getFields)
                .map(fields -> fields.length != 0)
                .orElse(false);
    }

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.field.general");
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return BundleManager.getFamilyName("plugin.generator.family.name");
    }
}
