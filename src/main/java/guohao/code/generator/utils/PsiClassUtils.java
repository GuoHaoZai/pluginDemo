/*
 *  Copyright (c) 2017-2019, bruce.ge.
 *    This program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU General Public License
 *    as published by the Free Software Foundation; version 2 of
 *    the License.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program;
 */

package guohao.code.generator.utils;

import com.intellij.psi.PsiModifier;
import guohao.code.generator.actions.MethodPrefixConstants;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

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
     * 抽取出类(包括父类)的SETTER方法
     */
    @NotNull
    public static List<PsiMethod> extractSetMethods(PsiClass psiClass) {
        return extractAllMethods(psiClass, PsiClassUtils::isSetMethod);
    }

    /**
     * 抽取出类(包括父类)的GETTER方法
     */
    @NotNull
    public static List<PsiMethod> extractGetMethods(PsiClass psiClass) {
        return extractAllMethods(psiClass, PsiClassUtils::isGetMethod);
    }

    /**
     * 检查当前类(<b>包括父类</b>)是否包含SETTER
     */
    public static boolean hasSetterMethod(final PsiClass psiClass) {
        return CollectionUtils.isNotEmpty(extractSetMethods(psiClass));
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
    private static boolean isNotSystemClass(PsiClass psiClass) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getQualifiedName)
                .map(qualifiedName -> !qualifiedName.startsWith("java."))
                .orElse(false);
    }

    /**
     * 判断当前方法是否是SETTER
     */
    private static boolean isSetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC) &&
                !m.hasModifierProperty(PsiModifier.STATIC) &&
                (m.getName().startsWith(MethodPrefixConstants.SET) || m.getName().startsWith(MethodPrefixConstants.WITH));
    }

    /**
     * 判断当前方法是否是GETTER
     */
    private static boolean isGetMethod(PsiMethod m) {
        return m.hasModifierProperty(PsiModifier.PUBLIC) && !m.hasModifierProperty(PsiModifier.STATIC) &&
                (m.getName().startsWith(MethodPrefixConstants.GET) || m.getName().startsWith(MethodPrefixConstants.IS));
    }

    /**
     * 抽取出当前类的指定方法
     */
    private static List<PsiMethod> extractMethods(PsiClass psiClass, Predicate<PsiMethod> predicate) {
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
    private static List<PsiMethod> extractAllMethods(final PsiClass psiClass, Predicate<PsiMethod> predicate) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getSupers)
                .map(Arrays::asList)
                .map(list->{
                    List<PsiClass> result = new ArrayList<>(list);
                    result.add(psiClass);
                    return result;
                })
                .orElse(Collections.emptyList()).stream()
                .filter(PsiClassUtils::isNotSystemClass)
                .map(clazz->extractMethods(clazz, predicate))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    //endregion


}
