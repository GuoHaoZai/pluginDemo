package guohao.common.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author guohao
 * @since 2021/1/22
 */
public final class PsiLocalVariableUtils {

    private PsiLocalVariableUtils(){}

    /**
     * 获取当前变量所有的SETTER
     *
     * @param localVariable
     * @return
     */
    @NotNull
    public static List<PsiMethod> getSetterMethods(PsiLocalVariable localVariable) {
        PsiClass psiClass = PsiTypesUtil.getPsiClass(localVariable.getType());
        return PsiClassUtils.extractSetMethods(psiClass);
    }

}
