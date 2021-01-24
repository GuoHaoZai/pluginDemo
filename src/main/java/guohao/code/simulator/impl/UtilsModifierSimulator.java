package guohao.code.simulator.impl;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import guohao.code.anno.Utils;
import guohao.code.simulator.ModifierSimulator;
import guohao.code.utils.ClassNameUtils;
import guohao.code.utils.PsiClassUtils;
import guohao.code.utils.PsiToolUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于处理{@link guohao.code.anno.Utils @Utils}修饰的类。
 *
 * <p>作用：</p>
 * <ol>
 *      <li> 对类中所有的Field、Method、InnerClass添加STATIC修饰符</li>
 *      <li>对当前类添加FINAL修饰符</li>
 * </ol>
 *
 * @author guohao
 * @apiNote 当前类仅仅是骗过IDEA编译器，在实际生成的代码中没有任何影响。
 * @since 2021/1/24
 */
public class UtilsModifierSimulator implements ModifierSimulator {

    private static final String CLASS_NAME = ClassNameUtils.getFullClassName(Utils.class);

    @Override
    public boolean isSupported(@NotNull PsiModifierList modifierList) {
        PsiElement parent = modifierList.getParent();

        if (PsiClassUtils.hasAnnotation(parent, CLASS_NAME)) {
            return validateOnAnnotated(((PsiClass) parent));
        }

        if (!PsiToolUtils.isField(parent) || !PsiToolUtils.isMethod(parent) || !PsiToolUtils.isInnerClass(parent)) {
            PsiClass containingClass = PsiTreeUtil.getParentOfType(parent, PsiClass.class, true);
            return PsiClassUtils.hasAnnotation(containingClass, CLASS_NAME) && validateOnAnnotated(containingClass);
        }

        return false;
    }

    @Override
    public Set<String> transformModifiers(@NotNull PsiModifierList modifierList, @NotNull Set<String> modifiers) {
        Set<String> result = new HashSet<>(modifiers);

        PsiElement parent = modifierList.getParent();

        if (PsiClassUtils.hasAnnotation(parent, CLASS_NAME)) {
            result.add(PsiModifier.FINAL);
        }

        if (PsiToolUtils.isField(parent) || PsiToolUtils.isMethod(parent) || PsiToolUtils.isInnerClass(parent)) {
            result.add(PsiModifier.STATIC);
        }
        return result;
    }

    //region utils
    private static boolean validateOnAnnotated(@NotNull PsiClass psiClass) {
        if (checkWrongType(psiClass)) {
            // utility class only supported on class
            return false;
        }
        PsiElement context = psiClass.getContext();
        if (context == null) {
            return false;
        }
        if (context instanceof PsiFile) {
            return true;
        }

        PsiElement contextUp = context;
        while (true) {
            if (contextUp instanceof PsiClass) {
                PsiClass psiClassUp = (PsiClass) contextUp;
                if (psiClassUp.getContext() instanceof PsiFile) {
                    return true;
                }
                boolean isStatic = PsiToolUtils.isStatic(psiClassUp.getModifierList());
                if (isStatic || checkWrongType(psiClassUp)) {
                    contextUp = contextUp.getContext();
                } else {
                    // utility class automatically makes class static
                    return false;
                }
            } else {
                // utility class cannot be placed
                return false;
            }
        }
    }

    private static boolean checkWrongType(PsiClass psiClass) {
        return psiClass != null && (psiClass.isInterface() || psiClass.isEnum() || psiClass.isAnnotationType());
    }

    //endregion
}
