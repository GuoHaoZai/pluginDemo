package guohao.anno;

import guohao.simulator.UtilsModifierSimulator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标志某个类是工具类。
 *
 * @author guohao
 * @see UtilsModifierSimulator
 * @since 2021/1/24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Utils {
}
