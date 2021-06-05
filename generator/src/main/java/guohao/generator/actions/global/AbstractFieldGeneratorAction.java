package guohao.generator.actions.global;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import guohao.common.PsiClassUtils;
import guohao.common.PsiDocumentUtils;
import guohao.common.PsiToolUtils;
import guohao.generator.BundleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * TODO 生成valid/添加注解
 * TODO 生成构造方法
 *
 * 在类的所有Field上添加指定字符串
 *
 * @author guohao
 * @since 2021/1/20
 */
public abstract class AbstractFieldGeneratorAction extends PsiElementBaseIntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        List<PsiField> psiFields = Optional.ofNullable(PsiTreeUtil.getParentOfType(element, PsiClass.class))
                .map(psiClass -> Arrays.asList(psiClass.getFields()))
                .orElse(Collections.emptyList());

        Optional<String> globalMessage = getGlobalMessage(project);
        for (PsiField psiField : psiFields) {
            globalMessage.ifPresent(message -> insertTextToFieldHeader(message, psiField));
            getFieldLocalMessage(psiField).ifPresent(message -> insertTextToFieldHeader(message, psiField));
        }
    }

    /**
     * 所有的field都添加一个字符串
     *
     * @return
     * @param project
     */
    protected Optional<String> getGlobalMessage(Project project) {
        return Optional.empty();
    }

    protected Optional<String> getFieldLocalMessage(PsiField psiField) {
        return Optional.empty();
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
        return PsiClassUtils.getContainingPsiClass(element)
                .map(PsiClass::getFields)
                .map(fields -> fields.length != 0)
                .orElse(Boolean.FALSE);
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return BundleManager.getFamilyName("plugin.generator.family.name");
    }
}
