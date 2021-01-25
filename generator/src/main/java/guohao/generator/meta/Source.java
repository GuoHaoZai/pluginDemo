package guohao.generator.meta;

import guohao.common.PsiToolUtils;
import com.intellij.psi.PsiElement;

import java.util.function.Predicate;

/**
 * @author guohao
 * @since 2021/1/21
 */
public enum Source {
    OTHER(-1, element -> true),
    JAVA(0, element -> true),
    GUAVA(1, PsiToolUtils::hasGuavaLibrary);

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
