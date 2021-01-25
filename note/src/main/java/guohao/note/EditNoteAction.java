package guohao.note;

import com.intellij.ide.bookmarks.BookmarkManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author guohao
 * @since 2021/1/25
 */
public class EditNoteAction extends AnAction {

    public EditNoteAction() {
        super("Set Bookmark with Description");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Optional.ofNullable(e.getProject())
                .map(BookmarkManager::getInstance)
                .ifPresent(bookmarkManager -> {
                    Optional.ofNullable(e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE))
                            .map(editor -> {
                                Document document = editor.getDocument();
                                int line = editor.getCaretModel().getLogicalPosition().line;
                                return bookmarkManager.findEditorBookmark(document, line);
                            })
                            .ifPresentOrElse(bookmark -> ActionManager.getInstance().getAction("EditBookmark").actionPerformed(e),
                                             () -> ActionManager.getInstance().getAction("ToggleBookmark").actionPerformed(e));
                });
    }
}
