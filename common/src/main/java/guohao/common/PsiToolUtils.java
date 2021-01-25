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

package guohao.common;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;

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
     * <p>'\n + 当前变量所在行行首的空格数'。</p>
     * <p>填充在生成的的语句前(格式化)。</p>
     */
    @NotNull
    public static String calculateFormatString(PsiElement element) {
        Document document = PsiDocumentUtils.getDocument(element);
        StringBuilder result = new StringBuilder("\n");

        int cur = element.getParent().getTextOffset();
        String text = "";
        do {
            result.append(text);
            text = document.getText(new TextRange(cur - 1, cur--));
        } while ((cur >= 1) && (text.equals(" ") || text.equals("\t")));
        return result.toString();
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
