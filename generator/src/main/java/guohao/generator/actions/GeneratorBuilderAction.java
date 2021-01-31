package guohao.generator.actions;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import guohao.common.*;
import guohao.generator.BundleManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author guohao
 * @since 2021/1/20
 */
public class GeneratorBuilderAction extends AbstractGeneratorAction {

    @Override
    protected boolean canExecute(@NotNull PsiLocalVariable localVariable) {
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        return PsiClassUtils.hasBuilderMethod(psiClass);
    }

    @Override
    protected void handleLocalVariable(@NotNull PsiLocalVariable localVariable) {
        Optional.of(localVariable)
                .flatMap(PsiClassUtils::getDeclarationPsiClass)
                .map(PsiClassUtils::extractBuildMethods)
                .flatMap(buildMethods -> buildMethods.stream().findFirst())
                .ifPresent(buildMethod -> {
                    String builderText = generateBuilderText(localVariable, buildMethod);
                    Document document = PsiDocumentUtils.getDocument(localVariable);
                    document.insertString(localVariable.getTextRange().getEndOffset(), builderText);
                    PsiDocumentUtils.commitAndSaveDocument(localVariable, document);
                });
    }

    /**
     * 生成builder文本端(格式化之后)
     *
     * @param localVariable 光标所在变量
     * @param buildMethod   outer class中的build方法
     * @return 可以直接插入的文本段
     */
    private String generateBuilderText(@NotNull PsiLocalVariable localVariable, @NotNull PsiMethod buildMethod) {
        PsiClass publicClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        PsiClass buildClass = PsiTypesUtil.getPsiClass(buildMethod.getReturnType());
        String formatString = PsiToolUtils.calculateLineHeaderToElementString(localVariable);

        String builderString = publicClass.getName() + "." + MethodPrefixConstants.BUILDER + "()\n";
        String buildString = "\n" + formatString + "\t\t" + "." + MethodPrefixConstants.BUILD + "();\n";

        StringJoiner result = PsiClassUtils.extractMethods(buildClass, PsiMethodUtils::isBuildFieldMethod).stream()
                .map(method -> generateBuilderMethodText(method, localVariable))
                .map(buildText -> "\t\t" + formatString + buildText)
                .reduce(new StringJoiner("\n", builderString, buildString),
                        StringJoiner::add,
                        (a, b) -> new StringJoiner(""));
        return result.toString();
    }

    private String generateBuilderMethodText(PsiMethod buildFiledMethod, PsiLocalVariable localVariable) {
        String getterName = Optional.of(buildFiledMethod)
                .map(PsiMethod::getName)
                .map(StringUtils::capitalize)
                .map(string -> MethodPrefixConstants.GET + string)
                .orElseThrow();

        return Optional.of(localVariable)
                .map(psiLocalVariable -> PsiTreeUtil.getParentOfType(psiLocalVariable, PsiMethod.class))
                .map(PsiMethodUtils::getPsiParameters)
                .map(parameters -> parameters.stream()
                        .filter(parameter -> PsiClassUtils.hasMethod(PsiTypesUtil.getPsiClass(parameter.getType()),
                                                                     method -> Objects.equals(method.getName(), getterName)))
                        .map(parameter -> "." + buildFiledMethod.getName() + "(" + parameter.getName() + "." + getterName + "())")
                        .findFirst()
                        .orElse("." + buildFiledMethod.getName() + "(null)"))
                .orElseThrow();
    }

    @NotNull
    @Override
    public String getText() {
        return BundleManager.getGeneratorBundle("generator.builder.method");
    }

}
