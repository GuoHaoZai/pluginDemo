package guohao.common;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

}
