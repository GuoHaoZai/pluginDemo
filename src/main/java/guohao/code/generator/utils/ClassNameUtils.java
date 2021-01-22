package guohao.code.generator.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @author guohao
 * @since 2021/1/21
 */
public final class ClassNameUtils {

    private ClassNameUtils() {}

    /**
     * 去除泛型
     */
    public static String removeGeneric(String className){
        String realClassName = StringUtils.trimToEmpty(className);
        if (realClassName.contains("<") && realClassName.endsWith(">")) {
            return realClassName.substring(0, realClassName.indexOf("<"));
        }
        return realClassName;
    }

}
