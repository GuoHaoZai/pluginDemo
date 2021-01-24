package guohao.code.simulator;

import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 对修饰符进行修改。
 *
 * @author guohao
 * @since 2021/1/23
 */
public interface ModifierSimulator {

    /**
     * 校验当前{@link ModifierSimulator}是否支持处理{@link PsiModifierList}的属性。
     * 当前方法不应该做复杂的计算。只是用于判断。
     *
     * @param modifierList 被扩充修饰符的修饰符列表
     * @return 是否需要执行transformModifiers方法
     */
    boolean isSupported(@NotNull PsiModifierList modifierList);

    /**
     * 计算当前{@link PsiModifierList}应该具有的修饰符并返回
     *
     * @param modifierList 被扩充修饰符的修饰符列表
     * @param modifiers    modifierList中当前存在的修饰符列表
     */
    Set<String> transformModifiers(@NotNull PsiModifierList modifierList, @NotNull final Set<String> modifiers);
}
