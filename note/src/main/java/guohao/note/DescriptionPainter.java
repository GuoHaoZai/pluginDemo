package guohao.note;

import com.google.common.collect.Lists;
import com.intellij.ide.bookmarks.BookmarkManager;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author guohao
 * @since 2021/1/25
 */
public class DescriptionPainter extends EditorLinePainter {

    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        return Optional.ofNullable(FileDocumentManager.getInstance().getDocument(file))
                .map(document -> {
                    BookmarkManager instance = BookmarkManager.getInstance(project);
                    return instance.findEditorBookmark(document, lineNumber);
                })
                .map(bookmark -> {
                    TextAttributes textAttributes = new TextAttributes();
                    textAttributes.setForegroundColor(JBColor.CYAN);
                    return Lists.newArrayList(new LineExtensionInfo(bookmark.getDescription(), textAttributes));
                })
                .orElse(new ArrayList<>(0));
    }
}
