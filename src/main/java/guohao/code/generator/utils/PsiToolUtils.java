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

package guohao.code.generator.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class PsiToolUtils {

    /**
     * 检查当前元素所在的模块是否包含guava类
     */
    public static boolean containGuava(@NotNull PsiElement element) {
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(element.getProject());
        return Optional.of(element)
                .map(ModuleUtilCore::findModuleForPsiElement)
                .map(moduleForPsiElement -> {
                    PsiClass[] lists = shortNamesCache.getClassesByName("Lists", GlobalSearchScope.moduleRuntimeScope(moduleForPsiElement, false));
                    return Arrays.asList(lists);
                })
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(psiClass -> Objects.equals(psiClass.getQualifiedName(), "com.google.common.collect.Lists"));
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
}
