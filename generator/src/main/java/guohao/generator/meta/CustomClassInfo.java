package guohao.generator.meta;

/**
 * @author guohao
 * @since 2021/1/21
 */
public class CustomClassInfo implements ClassInfo{

    private final String fullClassName;
    private final String packageName;
    private final String instance;
    private final Library library;

    public CustomClassInfo(String fullClassName, String packageName, String instance, Library library) {
        this.fullClassName = fullClassName;
        this.packageName = packageName;
        this.instance = instance;
        this.library = library;
    }

    @Override
    public String getFullClassName() {
        return fullClassName;
    }

    @Override
    public Library getLibrary() {
        return library;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getInstance() {
        return instance;
    }
}
