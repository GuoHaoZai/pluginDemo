package guohao.code.simulator;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用于生成需要告诉IDEA编译器的额外的信息。
 * 例如在类中添加内部类。
 *
 * @author guohao
 * @since 2021/1/23
 */
public interface Simulator {
    /**
     * 获取到当前{@link Simulator}能够处理的注解类。
     *
     * @return 注解全类名
     */
    @NotNull
    Set<String> getSupportedAnnotationClasses();

    /**
     * 获取到当前{@link Simulator}能够处理的类型。
     *
     * @return 例如Field、Method、Class
     */
    @NotNull
    Class<? extends PsiElement> getSupportedElementClass();

    default boolean notNameHintIsEqualToSupportedAnnotation(@Nullable String nameHint) {
        return null == nameHint || (!Objects.equals(nameHint, "simulator")
                && getSupportedAnnotationClasses().stream().map(StringUtil::getShortName).noneMatch(nameHint::equals));
    }

    /**
     * 模拟需要添加的元素
     *
     * @param element
     * @param nameHint
     * @return
     */
    @NotNull
    List<PsiElement> simulate(@NotNull PsiElement element, @Nullable String nameHint);

}
