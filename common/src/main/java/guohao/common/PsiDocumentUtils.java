package guohao.common;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * @author guohao
 * @since 2021/1/20
 */
public final class PsiDocumentUtils {

    private PsiDocumentUtils() {}

    public static void commitAndSaveDocument(PsiElement element, Document document) {
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(element.getProject());
        if (document != null) {
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
            psiDocumentManager.commitDocument(document);
            FileDocumentManager.getInstance().saveDocument(document);
        }
    }

    public static Document getDocument(PsiElement element){
        Project project = element.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile containingFile = element.getContainingFile();
        return psiDocumentManager.getDocument(containingFile);
    }
}
