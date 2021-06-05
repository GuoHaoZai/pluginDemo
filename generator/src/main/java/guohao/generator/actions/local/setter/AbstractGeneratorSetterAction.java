package guohao.generator.actions.local.setter;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import guohao.common.PsiClassUtils;
import guohao.common.PsiDocumentUtils;
import guohao.common.PsiJavaFileUtils;
import guohao.common.PsiToolUtils;
import guohao.generator.actions.local.AbstractGeneratorAction;
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
        Optional.of(localVariable)
                .flatMap(PsiClassUtils::getDeclarationPsiClass)
                .map(PsiClassUtils::extractSetMethods)
                .ifPresent(setters -> {
                    Set<String> newImportList = new HashSet<>();
                    Set<String> newSetterList = new HashSet<>();

                    for (PsiMethod setterMethod : setters) {
                        List<ClassInfo> classInfos = calculateNewSetterParamClassInfos(localVariable, setterMethod);

                        String newSetterText = generateSetterText(localVariable, setterMethod, classInfos);
                        newSetterList.add(newSetterText);

                        Set<String> importList = generateImportList(classInfos);
                        newImportList.addAll(importList);
                    }

                    Set<String> existedImportList = PsiJavaFileUtils.getImportLists(localVariable);
                    newSetterList.removeAll(existedImportList);

                    writeText(localVariable, newImportList, newSetterList);
                });
    }

    private Set<String> generateImportList(List<ClassInfo> classInfos) {
        return classInfos.stream()
                .map(ClassInfo::getPackageName)
                .collect(Collectors.toSet());
    }

    /**
     * 生成setter方法文本
     *
     * @param localVariable
     * @param setterMethod
     * @param classInfos
     * @return
     */
    private String generateSetterText(@NotNull PsiLocalVariable localVariable, PsiMethod setterMethod, List<ClassInfo> classInfos) {
        return classInfos.stream()
                .map(ClassInfo::getInstance)
                .reduce(new StringJoiner(",", localVariable.getName() + "." + setterMethod.getName() + "(", ");"),
                        StringJoiner::add,
                        (a, b) -> new StringJoiner(",", localVariable.getName() + "." + setterMethod.getName() + "(", ");"))
                .toString();
    }


    /**
     * 计算新生成的setter参数的类信息
     *
     * @param localVariable intention所在的变量
     * @param setterMethod  intention所在变量所在的方法
     */
    protected abstract List<ClassInfo> calculateNewSetterParamClassInfos(PsiLocalVariable localVariable, PsiMethod setterMethod);

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

            String setterStatementsText = generateSetterText(localVariable, newSetterList);

            if (StringUtils.isNotBlank(setterStatementsText)) {
                int startOffset = localVariable.getParent().getTextRange().getEndOffset();
                document.insertString(startOffset, setterStatementsText);
            }
        }
        // 写入IMPORT语句
        if (CollectionUtils.isNotEmpty(newImportList)) {

            String importStatementText = generateImportStatementsText(newImportList);

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
     * @param importList 需要写入的import语句列表
     * @return 可以写入文件的文本
     */
    private String generateImportStatementsText(Set<String> importList) {
        return importList.stream()
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
    private String generateSetterText(PsiElement element, Set<String> setterList) {
        String splitText = "\n" + PsiToolUtils.calculateLineHeaderToElementString(element);
        return setterList.stream()
                .reduce(new StringJoiner(splitText, splitText, "\n").setEmptyValue(StringUtils.EMPTY),
                        StringJoiner::add,
                        (j1, j2) -> new StringJoiner("").setEmptyValue(StringUtils.EMPTY))
                .toString();
    }

}
