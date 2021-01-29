package guohao.common;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author guohao
 * @since 2021/1/22
 */
public final class PsiMethodUtils {

    private PsiMethodUtils() {}

    /**
     * 获取方法的参数列表
     */
    @NotNull
    public static List<PsiParameter> getPsiParameters(PsiMethod psiMethod) {
        return Optional.ofNullable(psiMethod)
                .map(PsiMethod::getParameterList)
                .map(PsiParameterList::getParameters)
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    /**
     * 判断当前方法是否是SETTER
     */
    public static boolean isSetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC) &&
                !m.hasModifierProperty(PsiModifier.STATIC) &&
                (m.getName().startsWith(MethodPrefixConstants.SET));
    }

    /**
     * 判断当前方法是否是Builder
     */
    public static boolean isBuilderMethod(PsiMethod m){
        return m.getName().equals(MethodPrefixConstants.BUILDER) && m.hasModifierProperty(PsiModifier.STATIC);
    }

    /**
     * 判断当前方法是否是GETTER
     */
    public static boolean isGetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC) && !m.hasModifierProperty(PsiModifier.STATIC) &&
                (m.getName().startsWith(MethodPrefixConstants.GET) || m.getName().startsWith(MethodPrefixConstants.IS));
    }
}
