package guohao.generator.actions.local;

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
 * generator最抽象的层次。
 * <p>
 * 当前类及其子类只能对<b>本地变量</b>做处理。
 *
 * @author guohao
 * @since 2021/1/29
 */
public abstract class AbstractGeneratorAction extends PsiElementBaseIntentionAction {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        return Optional.of(psiElement)
                .map(element -> PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class))
                .filter(localVariable -> localVariable.getParent() instanceof PsiDeclarationStatement)
                .map(this::canExecute)
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
        Optional.ofNullable(PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class))
                .ifPresent(this::handleLocalVariable);
    }

    /**
     * 判断当前Generator是否可以执行
     */
    protected abstract boolean canExecute(@NotNull PsiLocalVariable localVariable);

    protected abstract void handleLocalVariable(@NotNull PsiLocalVariable localVariable);

}
