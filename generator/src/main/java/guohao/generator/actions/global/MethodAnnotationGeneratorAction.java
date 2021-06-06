package guohao.generator.actions.global;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
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
 * @author guohao
 * @since 2021/1/20
 */
public class MethodAnnotationGeneratorAction extends PsiElementBaseIntentionAction {

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        List<PsiMethod> psiMethods = Optional.ofNullable(PsiTreeUtil.getParentOfType(element, PsiClass.class))
                .map(psiClass -> Arrays.asList(psiClass.getMethods()))
                .orElse(Collections.emptyList());
        for (PsiMethod psiMethod : psiMethods) {
            if (!psiMethod.getModifierList().getText().contains("@NotNull")) {
                insertTextBeforeMethod("@NotNull", psiMethod);
            }
            for (PsiParameter parameter : psiMethod.getParameterList().getParameters()) {
                if (!parameter.getText().contains("@NotNull")) {
                    insertTextBeforeParameter("@NotNull", parameter);
                }
            }
        }
    }

    /**
     * 将字符串添加到parameter之前
     *
     * @param addedText 被添加的字符串
     * @param psiMethod method
     */
    private void insertTextBeforeMethod(String addedText, PsiMethod psiMethod) {
        Document document = PsiDocumentUtils.getDocument(psiMethod);
        TextRange textRange = psiMethod.getTextRange();
        String formatString = PsiToolUtils.calculateLineHeaderToElementString(psiMethod);
        document.insertString(textRange.getStartOffset(), addedText + "\n" + formatString);
        PsiDocumentUtils.commitAndSaveDocument(psiMethod, document);
    }

    /**
     * 将字符串添加到parameter之前
     *
     * @param addedText    被添加的字符串
     * @param psiParameter Parameter
     */
    private void insertTextBeforeParameter(String addedText, PsiParameter psiParameter) {
        Document document = PsiDocumentUtils.getDocument(psiParameter);
        TextRange textRange = psiParameter.getTextRange();
        document.insertString(textRange.getStartOffset(), addedText + " ");
        PsiDocumentUtils.commitAndSaveDocument(psiParameter, document);
    }

    /**
     * 只有当前类中包含Field时才可用
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiClassUtils.getContainingPsiClass(element)
                .map(PsiClass::getMethods)
                .map(methods -> methods.length != 0)
                .orElse(Boolean.FALSE);
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return BundleManager.getFamilyName("plugin.generator.family.name");
    }

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.method.annotation");
    }

}
