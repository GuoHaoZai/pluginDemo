package guohao.code.generator.enums;

/**
 * @author guohao
 * @since 2021/1/21
 */
public interface ClassInfo {

    /**
     * 全类名
     */
    String getFullClassName();

    /**
     * 类的来源
     */
    Source getSource();

    /**
     * 所在包名
     */
    String getPackageName();

    /**
     * 获取该类实例的方法
     */
    String getInstance();
}
