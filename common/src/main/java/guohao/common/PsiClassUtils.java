package guohao.common;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author guohao
 * @since 2021/1/20
 */
public final class PsiClassUtils {
    private PsiClassUtils() {}

    /**
     * 判断当前element是否包含指定的注解类
     */
    public static boolean hasAnnotation(PsiElement element, Class<? extends Annotation> annotationClass) {
        return element instanceof PsiClass && ((PsiClass) element).hasAnnotation(annotationClass.getName());
    }

    /**
     * 抽取出类(包括父类)的SETTER方法
     */
    @NotNull
    public static List<PsiMethod> extractSetMethods(PsiClass psiClass) {
        return extractAllMethods(psiClass, PsiMethodUtils::isSetMethod);
    }

    /**
     * 抽取出类(包括父类)的GETTER方法
     */
    @NotNull
    public static List<PsiMethod> extractGetMethods(PsiClass psiClass) {
        return extractAllMethods(psiClass, PsiMethodUtils::isGetMethod);
    }

    @NotNull
    public static List<PsiMethod> extractBuildMethods(PsiClass psiClass) {
        return extractAllMethods(psiClass, PsiMethodUtils::isBuilderMethod);
    }

    public static boolean hasMethod(PsiClass psiClass, Predicate<PsiMethod> psiMethodPredicate){
        return CollectionUtils.isNotEmpty(PsiClassUtils.extractAllMethods(psiClass, psiMethodPredicate));
    }

    /**
     * 检查当前类(<b>包括父类</b>)是否包含SETTER
     */
    public static boolean hasSetterMethod(final PsiClass psiClass) {
        return CollectionUtils.isNotEmpty(extractSetMethods(psiClass));
    }

    /**
     * 当前类是否包含Builder方法
     */
    public static boolean hasBuilderMethod(final PsiClass psiClass) {
        return CollectionUtils.isNotEmpty(extractBuildMethods(psiClass));
    }

    /**
     * 检查当前类(<b>包括父类</b>)是否包含GETTER
     */
    public static boolean hasGetterMethod(final PsiClass psiClass) {
        return CollectionUtils.isNotEmpty(extractGetMethods(psiClass));
    }

    //region 判断方法

    /**
     * 判断当前类是否是系统类(既jdk中的类)
     */
    public static boolean isNotSystemClass(PsiClass psiClass) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getQualifiedName)
                .map(qualifiedName -> !qualifiedName.startsWith("java."))
                .orElse(false);
    }

    /**
     * 抽取出当前类的指定方法
     */
    @NotNull
    public static List<PsiMethod> extractMethods(PsiClass psiClass, Predicate<PsiMethod> predicate) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getMethods)
                .map(Arrays::asList)
                .orElse(Collections.emptyList()).stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * 抽取出类(包括父类,不包括系统类)的GETTER方法
     */
    @NotNull
    public static List<PsiMethod> extractAllMethods(final PsiClass psiClass, Predicate<PsiMethod> predicate) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getSupers)
                .map(Arrays::asList)
                .map(list -> {
                    List<PsiClass> result = new ArrayList<>(list);
                    result.add(psiClass);
                    return result;
                })
                .orElse(Collections.emptyList()).stream()
                .filter(PsiClassUtils::isNotSystemClass)
                .map(clazz -> extractMethods(clazz, predicate))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    //endregion
}
