package com.generator.utils;

import com.intellij.psi.PsiMethod;
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
public class PsiMethodUtils {

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
}
