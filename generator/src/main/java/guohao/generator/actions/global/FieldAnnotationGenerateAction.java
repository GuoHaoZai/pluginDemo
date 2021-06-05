package guohao.generator.actions.global;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiField;
import guohao.generator.BundleManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * @author guohao
 * @since 2021/6/6
 */
public class FieldAnnotationGenerateAction extends AbstractFieldGeneratorAction {

    private static final Set<String> baseType = Sets.newHashSet("byte", "boolean", "char", "short", "int", "long", "float", "double");

    @Override
    protected Optional<String> getFieldLocalMessage(PsiField psiField) {
        String typeString = StringUtils.deleteWhitespace(psiField.getType().getPresentableText());
        if (typeString.equals("String")) {
            if (!psiField.getText().contains("@NotEmpty")) {
                return Optional.of("@NotEmpty");
            }
            return Optional.empty();
        }

        if (!baseType.contains(typeString)) {
            if (!psiField.getText().contains("@NotNull")) {
                return Optional.of("@NotNull");
            }
            return Optional.empty();
        }

        return Optional.empty();
    }

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.field.annotation");
    }

}
