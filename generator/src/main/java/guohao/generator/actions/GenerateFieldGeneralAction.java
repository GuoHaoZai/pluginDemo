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
 * 在类的所有Field上添加指定字符串
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
                    .map(psiClass -> Arrays.asList(psiClass.getFields()))
                    .ifPresent(psiFields -> psiFields.forEach(psiField -> insertTextToFieldHeader(fieldString, psiField)));
        }
    }

    /**
     * 将字符串添加到field头
     *
     * @param addedText 被添加的字符串
     * @param psiField  field
     */
    private void insertTextToFieldHeader(String addedText, PsiField psiField) {
        Document document = PsiDocumentUtils.getDocument(psiField);
        TextRange textRange = psiField.getTextRange();
        String formatString = PsiToolUtils.calculateLineHeaderToElementString(psiField);
        document.insertString(textRange.getStartOffset(), addedText + "\n" + formatString);
        PsiDocumentUtils.commitAndSaveDocument(psiField, document);
    }

    /**
     * 只有当前类中包含Field时才可用
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return Optional.of(element)
                .map(psiElement -> PsiTreeUtil.getParentOfType(psiElement, PsiClass.class))
                .map(PsiClass::getFields)
                .map(fields -> fields.length != 0)
                .orElse(Boolean.FALSE);
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
