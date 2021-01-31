package guohao.common;

import com.intellij.psi.*;
import org.apache.commons.collections.CollectionUtils;
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

    private PsiMethodUtils() {
    }

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
    public static boolean isBuilderMethod(PsiMethod m) {
        return m.getName().equals(MethodPrefixConstants.BUILDER) && m.hasModifierProperty(PsiModifier.STATIC);
    }

    /**
     * 判断当前方法是否是GETTER
     */
    public static boolean isGetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC) && !m.hasModifierProperty(PsiModifier.STATIC) &&
                (m.getName().startsWith(MethodPrefixConstants.GET) || m.getName().startsWith(MethodPrefixConstants.IS));
    }

    /**
     * 判断当前方法是否是Builder中Field方法
     *
     * 例如：以下案例中，i方法就是一个field方法
     * <pre>
     *     public class Main{
     *          public static class Builder{
     *              private Integer i;
     *
     *              public Builder i(Integer i){
     *                  this.i = i;
     *              }
     *
     *              public Main build(){
     *                  return new Main();
     *              }
     *          }
     *     }
     * </pre>
     *
     * @param method
     * @return
     */
    public static boolean isBuildFieldMethod(PsiMethod method) {
        return !method.isConstructor() && !method.getName().equals("toString")
                && !method.getName().equals(MethodPrefixConstants.BUILD)
                && CollectionUtils.isNotEmpty(getPsiParameters(method));
    }
}
