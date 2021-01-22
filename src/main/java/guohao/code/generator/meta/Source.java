package guohao.code.generator.meta;

import guohao.code.generator.utils.PsiToolUtils;
import com.intellij.psi.PsiElement;

import java.util.function.Predicate;

/**
 * @author guohao
 * @since 2021/1/21
 */
public enum Source {
    OTHER(-1, PsiToolUtils::containGuava),
    JAVA(0, element -> true),
    GUAVA(1, PsiToolUtils::containGuava);

    private final Integer index;
    private final Predicate<PsiElement> canUse;

    Source(Integer index, Predicate<PsiElement> canUse) {
        this.index = index;
        this.canUse = canUse;
    }

    public Integer getIndex() {
        return index;
    }

    public Predicate<PsiElement> getCanUse() {
        return canUse;
    }
}
