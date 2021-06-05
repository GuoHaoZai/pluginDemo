package guohao.generator.actions.local.builder;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import guohao.common.*;
import guohao.generator.BundleManager;
import guohao.generator.actions.local.AbstractGeneratorAction;
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

        return PsiClassUtils.extractMethods(buildClass, PsiMethodUtils::isBuildFieldMethod).stream()
                .map(method -> generateBuilderMethodText(method, localVariable))
                .map(buildText -> "\t\t" + formatString + buildText)
                .reduce(new StringJoiner("\n", builderString, buildString),
                        StringJoiner::add,
                        (a, b) -> new StringJoiner(""))
                .toString();
    }

    private String generateBuilderMethodText(PsiMethod buildFiledMethod, PsiLocalVariable localVariable) {
        String buildMethodName = buildFiledMethod.getName();
        String getterMethodName = MethodPrefixConstants.GET + StringUtils.capitalize(buildMethodName);

        return Optional.of(localVariable)
                .map(psiLocalVariable -> PsiTreeUtil.getParentOfType(psiLocalVariable, PsiMethod.class))
                .map(PsiMethodUtils::getPsiParameters)
                .map(parameters -> parameters.stream()
                        .map(parameter -> {
                            if (StringUtils.equals(parameter.getName(), buildMethodName)) {
                                return "." + buildMethodName + "(" + parameter.getName() +")";
                            }
                            PsiClass psiClass = PsiTypesUtil.getPsiClass(parameter.getType());
                            if (PsiClassUtils.hasMethod(psiClass, method -> Objects.equals(method.getName(), getterMethodName))) {
                                return "." + buildFiledMethod.getName() + "(" + parameter.getName() + "." + getterMethodName + "())";
                            }
                            return StringUtils.EMPTY;
                        })
                        .filter(StringUtils::isNotEmpty)
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
