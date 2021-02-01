package guohao.common;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guohao
 * @since 2021/1/30
 */
public class PsiJavaFileUtils {

    private PsiJavaFileUtils() {
    }

    @NotNull
    public static Optional<PsiJavaFile> getPsiJavaFile(PsiElement element) {
        return Optional.ofNullable(element)
                .map(PsiElement::getContainingFile)
                .filter(file -> file instanceof PsiJavaFile)
                .map(file -> (PsiJavaFile) file);
    }

    /**
     * 获取java文件中的import语句
     */
    @NotNull
    public static List<PsiImportStatement> getImportList(PsiJavaFile psiJavaFile) {
        return Optional.ofNullable(psiJavaFile.getImportList())
                .map(PsiImportList::getImportStatements)
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    /**
     * 获取元素所在java文件import列表的全类名
     *
     * @param element
     * @return
     */
    @NotNull
    public static Set<String> getImportLists(@NotNull PsiElement element) {
        return Optional.of(element)
                .flatMap(PsiJavaFileUtils::getPsiJavaFile)
                .map(PsiJavaFileUtils::getImportList)
                .map(importStatementList -> importStatementList.stream().map(PsiImportStatement::getQualifiedName).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }
}
