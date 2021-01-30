package guohao.generator.meta;

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
     * 所属类库
     */
    Library getLibrary();

    /**
     * 所在包名
     */
    String getPackageName();

    /**
     * 获取该类实例的方法
     */
    String getInstance();
}
