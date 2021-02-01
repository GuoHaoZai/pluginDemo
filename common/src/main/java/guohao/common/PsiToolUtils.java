package guohao.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author guohao
 * @since 2021/1/20
 */
public final class PsiToolUtils {

    private PsiToolUtils() {
    }

    /**
     * 检查当前元素所在的模块是否包含guava库
     */
    public static boolean hasGuavaLibrary(@NotNull PsiElement element) {
        return hasSpecialLibrary(element.getProject(), GlobalConstants.GUAVA_PACKAGE);
    }

    /**
     * 检查当前元素所在的模块是否包含simulator库
     */
    public static boolean hasSimulatorLibrary(@NotNull PsiElement element) {
        return hasSpecialLibrary(element.getProject(), GlobalConstants.SIMULATOR_PACKAGE);
    }

    /**
     * 判断当前项目中是否包含指定的库
     *
     * @param project     项目
     * @param packageName 包路径
     */
    public static boolean hasSpecialLibrary(@NotNull Project project, String packageName) {
        ApplicationManager.getApplication().assertReadAccessAllowed();
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> {
            PsiPackage aPackage = JavaPsiFacade.getInstance(project).findPackage(packageName);
            return new CachedValueProvider.Result<>(aPackage, ProjectRootManager.getInstance(project));
        }) != null;
    }

    /**
     * <p>计算给定PsiElement头到所在行行首的字符串。</p>
     * <p>用于填充在生成的的语句前(格式化)。</p>
     *
     * 例如：element取下面的i,那返回值为"    "
     * <pre>
     *     public class Main{
     *         private Integer i;
     *     }
     * </pre>
     */
    @NotNull
    public static String calculateLineHeaderToElementString(PsiElement element) {
        return Optional.ofNullable(element)
                .map(PsiDocumentUtils::getDocument)
                .map(document -> document.getText(new TextRange(0, element.getTextRange().getStartOffset())))
                .map(String::toCharArray)
                .map(chars -> {
                    StringBuilder result = new StringBuilder();
                    for (int i = chars.length - 1; i >= 0 && chars[i] != '\n'; i--) {
                        result.append(chars[i]);
                    }
                    return result.toString();
                })
                .orElse("");
    }

    public static boolean isInnerClass(PsiElement element) {
        return (element instanceof PsiClass && element.getParent() instanceof PsiClass && !((PsiClass) element.getParent()).isInterface());
    }

    public static boolean isStatic(PsiModifierList modifierList) {
        return modifierList != null && modifierList.hasModifierProperty(PsiModifier.STATIC);
    }

    public static boolean isField(PsiElement element) {
        return element instanceof PsiField;
    }

    public static boolean isMethod(PsiElement element) {
        return element instanceof PsiMethod;
    }
}
