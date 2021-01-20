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

package com.generator.utils;

import com.generator.Parameters;
import com.generator.RealParam;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTypesUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class PsiToolUtils {

    public static boolean checkGuavaExist(Project project, @NotNull PsiElement element) {
        Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(element);
        if(moduleForPsiElement==null){
            return false;
        }
        PsiClass[] lists = PsiShortNamesCache.getInstance(project).getClassesByName("Lists", GlobalSearchScope.moduleRuntimeScope(moduleForPsiElement, false));
        for (PsiClass psiClass : lists) {
            if (Objects.equals(psiClass.getQualifiedName(), "com.google.common.collect.Lists")){
                return true;
            }
        }

        return false;
    }

    @NotNull
    public static Parameters extractParamInfo(PsiType psiType) {
        String typeFullName = psiType.getCanonicalText();
        Parameters info = new Parameters();
        info.setReturnType(PsiTypesUtil.getPsiClass(psiType));
        int u = typeFullName.indexOf("<");
        if (u == -1) {
            List<RealParam> realParamList = new ArrayList<>();
            RealParam real = new RealParam(typeFullName, extractShortName(typeFullName));
            realParamList.add(real);
            info.setParams(realParamList);
        } else {
            String collectpart = typeFullName.substring(0, u);
            String realClassPart = typeFullName.substring(u + 1, typeFullName.length() - 1);
            info.setCollectName(extractShortName(collectpart));
            info.setCollectPackege(collectpart);
            String[] split = realClassPart.split(",");
            List<RealParam> params = new ArrayList<>();
            if (split.length > 0) {
                for (String m : split) {
                    RealParam param = new RealParam(m,extractShortName(m));
                    params.add(param);
                }
            }
            info.setParams(params);
        }
        return info;
    }

    public static void addImportToFile(PsiDocumentManager psiDocumentManager, PsiJavaFile containingFile, Document document, Set<String> newImportList) {
        if (newImportList.size() > 0) {
            Iterator<String> iterator = newImportList.iterator();
            while (iterator.hasNext()) {
                String u = iterator.next();
                if (u.startsWith("java.lang")) {
                    iterator.remove();
                }
            }
        }

        if (newImportList.size() > 0) {
            PsiJavaFile javaFile = containingFile;
            PsiImportStatement[] importStatements = javaFile.getImportList().getImportStatements();
            Set<String> containedSet = new HashSet<>();
            for (PsiImportStatement s : importStatements) {
                containedSet.add(s.getQualifiedName());
            }
            StringBuilder newImportText = new StringBuilder();
            for (String newImport : newImportList) {
                if (!containedSet.contains(newImport)) {
                    newImportText.append("\nimport " + newImport + ";");
                }
            }
            PsiPackageStatement packageStatement = javaFile.getPackageStatement();
            int start = 0;
            if (packageStatement != null) {
                start = packageStatement.getTextLength() + packageStatement.getTextOffset();
            }
            String insertText = newImportText.toString();
            if (StringUtils.isNotBlank(insertText)) {
                document.insertString(start, insertText);
                PsiDocumentUtils.commitAndSaveDocument(psiDocumentManager, document);
            }
        }
    }

    @NotNull
    public static String lowerStart(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    private static String extractShortName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1);
    }

    @NotNull
    public static String calculateSplitText(Document document, int statementOffset) {
        String splitText = "";
        int cur = statementOffset;
        String text = document.getText(new TextRange(cur - 1, cur));
        while (text.equals(" ") || text.equals("\t")) {
            splitText = text + splitText;
            cur--;
            if (cur < 1) {
                break;
            }
            text = document.getText(new TextRange(cur - 1, cur));
        }
        splitText = "\n" + splitText;
        return splitText;
    }
}
