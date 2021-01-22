package guohao.code.generator.actions;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import guohao.code.generator.actions.impl.GeneratorConfig;
import guohao.code.generator.constant.MenuNameConstants;
import guohao.code.generator.constant.MethodPrefixConstants;
import guohao.code.generator.meta.BasicClassInfo;
import guohao.code.generator.meta.ClassInfo;
import guohao.code.generator.meta.CustomClassInfo;
import guohao.code.generator.meta.Source;
import guohao.code.generator.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guohao
 * @since 2021/1/20
 */
public abstract class GenerateAllSetterBase extends PsiElementBaseIntentionAction {
    private final GeneratorConfig generatorConfig = getGeneratorConfig();

    public abstract GeneratorConfig getGeneratorConfig();

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiElement psiParent = PsiTreeUtil.getParentOfType(element, PsiLocalVariable.class, PsiMethod.class);
        if (psiParent == null) {
            return;
        }
        if (psiParent instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocal = (PsiLocalVariable) psiParent;
            handleLocalVariable(psiLocal);
        }
    }

/**     本地变量处理         */

    //region 处理本地变量
    private void handleLocalVariable(PsiLocalVariable localVariable) {
        if (!(localVariable.getParent() instanceof PsiDeclarationStatement)) {
            return;
        }

        Set<String> newImportList = new HashSet<>();
        Set<String> newSetterList = new HashSet<>();

        for (PsiMethod setterMethod : PsiLocalVariableUtils.getSetterMethods(localVariable)) {
            StringJoiner setterStatement = new StringJoiner(",", localVariable.getName() + "." + setterMethod.getName() + "(", ");");
            for (ClassInfo paramInfo : setterMethodParamInfos(localVariable, setterMethod)) {
                setterStatement.add(paramInfo.getInstance());
                newImportList.add(paramInfo.getPackageName());
            }
            newSetterList.add(setterStatement.toString());
        }

        writeText(localVariable, newImportList, newSetterList);
    }

    private List<ClassInfo> setterMethodParamInfos(PsiLocalVariable localVariable,
                                                   PsiMethod setterMethod) {
        if (generatorConfig.fromParam()) {
            return buildSetterFromArgs(localVariable, setterMethod);
        }
        if (!generatorConfig.shouldAddDefaultValue()) {
            return Collections.singletonList(buildSetterNoParam());
        }
        return parseMethod(setterMethod);
    }

    /**
     * 将给定的列表写入文件中
     *
     * @param localVariable 用于确认文本写入位置
     * @param newImportList 需要写入的import语句列表
     * @param newSetterList 需要写入的SETTER语句列表
     */
    private void writeText(PsiLocalVariable localVariable, Set<String> newImportList, Set<String> newSetterList) {
        Document document = PsiDocumentUtils.getDocument(localVariable);
        // 写入SETTER语句
        if (CollectionUtils.isNotEmpty(newSetterList)) {

            String setterStatementsText = getSetterStatementsText(localVariable, newSetterList);

            if (StringUtils.isNotBlank(setterStatementsText)) {
                int startOffset = localVariable.getParent().getTextRange().getEndOffset();
                document.insertString(startOffset, setterStatementsText);
            }
        }
        // 写入IMPORT语句
        if (CollectionUtils.isNotEmpty(newImportList)) {

            String importStatementText = getImportStatementText(localVariable, newImportList);

            if (StringUtils.isNotBlank(importStatementText)) {
                Integer startOffset = Optional.of(localVariable)
                        .map(variable -> (PsiJavaFile) variable.getContainingFile())
                        .map(PsiJavaFile::getPackageStatement)
                        .map(packageStatement -> packageStatement.getTextLength() + packageStatement.getTextOffset())
                        .orElse(0);
                document.insertString(startOffset, importStatementText);
            }
        }
        PsiDocumentUtils.commitAndSaveDocument(localVariable, document);
    }

    /**
     * 将给定的import列表转化为可以写入文件的文本(有格式)
     *
     * @param element    获取已经存在的import语句并进行去重
     * @param importList 需要写入的import语句列表
     * @return 可以写入文件的文本
     */
    private String getImportStatementText(PsiElement element, Set<String> importList) {
        Set<String> existedImportList = Optional.of(element)
                .map(variable -> (PsiJavaFile) variable.getContainingFile())
                .map(PsiJavaFile::getImportList)
                .map(PsiImportList::getImportStatements)
                .map(Arrays::asList).orElse(Collections.emptyList())
                .stream()
                .map(PsiImportStatement::getQualifiedName)
                .collect(Collectors.toSet());

        return importList.stream()
                .filter(StringUtils::isEmpty)
                .filter(existedImportList::contains)
                .map(importStatement -> "import " + importStatement + ";")
                .reduce(new StringJoiner("\n", "\n", "\n").setEmptyValue(StringUtils.EMPTY),
                        StringJoiner::add,
                        (j1, j2) -> new StringJoiner(StringUtils.EMPTY).setEmptyValue(StringUtils.EMPTY))
                .toString();
    }

    /**
     * 将给定的SETTER列表转化为可以写入文件的文本(有格式)
     *
     * @param element    通过PSI模型获取格式
     * @param setterList 需要写入的SETTER语句列表
     * @return 可以写入文件的文本
     */
    private String getSetterStatementsText(PsiElement element, Set<String> setterList) {
        String splitText = PsiToolUtils.calculateFormatString(element);
        return setterList.stream()
                .reduce(new StringJoiner(splitText, splitText, "\n").setEmptyValue(StringUtils.EMPTY),
                        StringJoiner::add,
                        (j1, j2) -> new StringJoiner("").setEmptyValue(StringUtils.EMPTY))
                .toString();
    }

    //endregion

    //region 生成默认参数
    /**
     * 解析SETTER方法
     */
    private List<ClassInfo> parseMethod(PsiMethod setterMethod) {
        return Optional.of(setterMethod)
                .map(PsiMethodUtils::getPsiParameters).orElse(Collections.emptyList())
                .stream()
                .map(this::parseParameter)
                .collect(Collectors.toList());
    }

    /**
     * 根据每一个参数的类型
     * @param parameter
     * @return
     */
    @NotNull
    private ClassInfo parseParameter(PsiParameter parameter) {
        return Optional.ofNullable(parameter)
                .map(PsiParameter::getType)
                .map(PsiType::getCanonicalText)
                .map(ClassNameUtils::removeGeneric)
                .map(BasicClassInfo::get).orElse(Collections.emptyList())
                .stream()
                .filter(basicClassInfo -> basicClassInfo.getSource().getCanUse().test(parameter))
                .min(Comparator.comparingInt(cl -> cl.getSource().getIndex()))
                .orElse(BasicClassInfo.NULL);
    }
    //endregion

    //region 生成空参数
    /**
     * SETTER的入参为空
     */
    private ClassInfo buildSetterNoParam() {
        return new CustomClassInfo("", "", "", Source.OTHER);
    }
    //endregion

    //region 从方法入参提取参数

    private List<ClassInfo> buildSetterFromArgs(PsiLocalVariable localVariable, PsiMethod setterMethod){
        List<PsiParameter> methodParameters = Optional.of(localVariable)
                .map(variable -> PsiTreeUtil.getParentOfType(variable, PsiMethod.class))
                .map(PsiMethodUtils::getPsiParameters).orElse(Collections.emptyList());

        return Optional.of(setterMethod)
                .map(PsiMethodUtils::getPsiParameters).orElse(Collections.emptyList())
                .stream()
                .map(parameter->parseParameters(methodParameters, setterMethod))
                .collect(Collectors.toList());
    }

    private ClassInfo parseParameters(List<PsiParameter> methodParameters, PsiMethod setterMethod) {
        for (PsiParameter methodParameter : methodParameters) {
            Optional<ClassInfo> classInfoOpt = parseParameter(setterMethod, methodParameter);
            if (classInfoOpt.isPresent()) {
                return classInfoOpt.get();
            }
        }
        return BasicClassInfo.NULL;
    }

    private Optional<ClassInfo> parseParameter(PsiMethod setterMethod, PsiParameter methodParameter) {
        String resultName = setterMethod.getName().replaceFirst(MethodPrefixConstants.SET, MethodPrefixConstants.GET);
        return Optional.of(methodParameter)
                .map(PsiParameter::getType)
                .map(PsiTypesUtil::getPsiClass)
                .map(PsiClassUtils::extractGetMethods)
                .stream()
                .flatMap(Collection::stream)
                .filter(method -> method.getName().equals(resultName))
                .<ClassInfo>map(method -> {
                    // TODO 根据参数匹配
                    String instance = methodParameter.getName() + "." + method.getName() + "()";
                    return new CustomClassInfo("", "", instance, Source.OTHER);
                })
                .findFirst();
    }

    //endregion

    //region 工具方法


    //endregion

    /** 目前不知道的*/
    //region unknown
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        PsiClass psiClass = PsiClassUtils.buildFrom(element);
        if (psiClass == null) {
            return false;
        }
        return PsiClassUtils.hasSetterMethod(psiClass);
    }


    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return MenuNameConstants.GENERATOR;
    }
    //endregion

}
