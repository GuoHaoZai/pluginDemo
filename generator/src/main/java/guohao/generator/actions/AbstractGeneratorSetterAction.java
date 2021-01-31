package guohao.generator.actions;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import guohao.common.PsiClassUtils;
import guohao.common.PsiDocumentUtils;
import guohao.common.PsiJavaFileUtils;
import guohao.common.PsiToolUtils;
import guohao.generator.meta.ClassInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SETTER方法Generator抽象。
 *
 * @author guohao
 * @since 2021/1/20
 */
public abstract class AbstractGeneratorSetterAction extends AbstractGeneratorAction {

    @Override
    protected boolean canExecute(@NotNull PsiLocalVariable localVariable) {
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        return PsiClassUtils.hasSetterMethod(psiClass);
    }

    /**
     * 本地变量处理
     */
    @Override
    protected void handleLocalVariable(@NotNull PsiLocalVariable localVariable) {
        Set<String> newImportList = new HashSet<>();
        Set<String> newSetterList = new HashSet<>();

        for (PsiMethod setterMethod : PsiClassUtils.extractSetMethods(PsiTypesUtil.getPsiClass(localVariable.getType()))) {
            StringJoiner setterStatement = new StringJoiner(",", localVariable.getName() + "." + setterMethod.getName() + "(", ");");
            for (ClassInfo paramInfo : parseMethod(localVariable, setterMethod)) {
                setterStatement.add(paramInfo.getInstance());
                newImportList.add(paramInfo.getPackageName());
            }
            newSetterList.add(setterStatement.toString());
        }

        writeText(localVariable, newImportList, newSetterList);
    }

    /**
     * @param localVariable intention所在的变量
     * @param setterMethod  intention所在变量所在的方法
     */
    protected abstract List<ClassInfo> parseMethod(PsiLocalVariable localVariable, PsiMethod setterMethod);

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

            String setterStatementsText = generateSetterStatementsText(localVariable, newSetterList);

            if (StringUtils.isNotBlank(setterStatementsText)) {
                int startOffset = localVariable.getParent().getTextRange().getEndOffset();
                document.insertString(startOffset, setterStatementsText);
            }
        }
        // 写入IMPORT语句
        if (CollectionUtils.isNotEmpty(newImportList)) {

            String importStatementText = generateImportStatementsText(localVariable, newImportList);

            if (StringUtils.isNotBlank(importStatementText)) {
                Integer startOffset = Optional.of(localVariable)
                        .flatMap(PsiJavaFileUtils::getPsiJavaFile)
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
    private String generateImportStatementsText(PsiElement element, Set<String> importList) {
        Set<String> existedImportList = Optional.of(element)
                .flatMap(PsiJavaFileUtils::getPsiJavaFile)
                .map(PsiJavaFileUtils::getImportList)
                .map(importStatementList -> importStatementList.stream().map(PsiImportStatement::getQualifiedName).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
        return importList.stream()
                .filter(StringUtils::isEmpty)
                .filter(importStatement-> !existedImportList.contains(importStatement))
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
    private String generateSetterStatementsText(PsiElement element, Set<String> setterList) {
        String splitText = "\n" + PsiToolUtils.calculateLineHeaderToElementString(element);
        return setterList.stream()
                .reduce(new StringJoiner(splitText, splitText, "\n").setEmptyValue(StringUtils.EMPTY),
                        StringJoiner::add,
                        (j1, j2) -> new StringJoiner("").setEmptyValue(StringUtils.EMPTY))
                .toString();
    }

}
