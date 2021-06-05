package guohao.generator.actions.global;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import guohao.generator.BundleManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * TODO 生成valid/添加注解
 * TODO 生成构造方法
 * <p>
 * 在类的所有Field上添加指定字符串
 *
 * @author guohao
 * @since 2021/1/20
 */
public class FieldStringGeneratorAction extends AbstractFieldGeneratorAction {

    @Override
    protected Optional<String> getGlobalMessage(Project project) {
        String fieldString = Messages.showMultilineInputDialog(project, "", "Message", null, Messages.getInformationIcon(), null);
        if (StringUtils.isBlank(fieldString)) {
            return Optional.empty();
        }
        return Optional.of(fieldString);
    }

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.field.general");
    }
}
