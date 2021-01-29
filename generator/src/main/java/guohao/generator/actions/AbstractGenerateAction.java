package guohao.generator.actions;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDeclarationStatement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.util.PsiTreeUtil;
import guohao.generator.BundleManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author guohao
 * @since 2021/1/29
 */
public abstract class AbstractGenerateAction extends PsiElementBaseIntentionAction {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return Optional.of(psiElement)
                .map(element -> PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class))
                .filter(localVariable -> localVariable.getParent() instanceof PsiDeclarationStatement)
                .map(this::isExecute)
                .orElse(Boolean.FALSE);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return BundleManager.getFamilyName("plugin.generator.family.name");
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiLocalVariable localVariable = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class);
        handleLocalVariable(localVariable);
    }

    protected abstract boolean isExecute(@NotNull PsiLocalVariable localVariable);

    protected abstract void handleLocalVariable(PsiLocalVariable localVariable);

}
