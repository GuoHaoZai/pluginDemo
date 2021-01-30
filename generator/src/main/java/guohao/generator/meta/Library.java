package guohao.generator.meta;

import com.intellij.psi.PsiElement;
import guohao.common.PsiToolUtils;

import java.util.function.Predicate;

/**
 * 类库。用于表示{@link ClassInfo}对象所属类库。
 * 并且还包含了使用当前类库需要满足的条件。
 *
 * @author guohao
 * @since 2021/1/21
 */
public enum Library {
    OTHER(-1, element -> true),
    JAVA(0, element -> true),
    GUAVA(1, PsiToolUtils::hasGuavaLibrary);

    /**
     * 优先级
     */
    private final Integer index;

    /**
     * 使用该类库必须满足的条件
     */
    private final Predicate<PsiElement> canUse;

    Library(Integer index, Predicate<PsiElement> canUse) {
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
