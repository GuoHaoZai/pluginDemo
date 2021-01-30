package guohao.common;

import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author guohao
 * @since 2021/1/30
 */
public class PsiJavaFileUtils {

    private PsiJavaFileUtils() {
    }

    /**
     * 获取java文件中的import语句
     */
    @NotNull
    public static List<PsiImportStatement> getImportList(PsiJavaFile psiJavaFile) {
        PsiImportList existImportList = psiJavaFile.getImportList();
        if (Objects.isNull(existImportList)) {
            return Collections.emptyList();
        }
        PsiImportStatement[] importStatements = existImportList.getImportStatements();
        return Arrays.asList(importStatements);
    }

}
