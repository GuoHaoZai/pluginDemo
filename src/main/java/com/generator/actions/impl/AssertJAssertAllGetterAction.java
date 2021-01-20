package com.generator.actions.impl;

import com.generator.actions.GenerateAllSetterBase;
import com.generator.utils.MenuNameConstants;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class AssertJAssertAllGetterAction extends GenerateAllSetterBase {

    @NotNull
    @Override
    public String getText() {
        return MenuNameConstants.ASSERTALLPROPS;
    }

    @Override
    public GeneratorConfig getGeneratorConfig() {
        return new GeneratorConfig() {
            @Override
            public boolean isSetter() {
                return false;
            }

            @Override
            public String formatLine(String line) {
                return "assertThat(" + line.substring(0, line.length() - 1) + ").isEqualTo();";
            }
        };
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if(containingFile==null){
            return false;
        }
        VirtualFile virtualFile = containingFile.getVirtualFile();
        if(virtualFile==null){
            return false;
        }
        boolean inTestSourceContent = ProjectRootManager.getInstance(element.getProject()).getFileIndex().isInTestSourceContent(virtualFile);
        if (inTestSourceContent) {
            return super.isAvailable(project, editor, element);
        }
        return false;
    }
}
