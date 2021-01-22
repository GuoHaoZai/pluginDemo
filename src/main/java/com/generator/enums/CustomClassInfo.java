package com.generator.enums;

/**
 * @author guohao
 * @since 2021/1/21
 */
public class CustomClassInfo implements ClassInfo{

    private final String fullClassName;
    private final String packageName;
    private final String instance;
    private final Source source;

    public CustomClassInfo(String fullClassName, String packageName, String instance, Source source) {
        this.fullClassName = fullClassName;
        this.packageName = packageName;
        this.instance = instance;
        this.source = source;
    }

    @Override
    public String getFullClassName() {
        return fullClassName;
    }

    @Override
    public Source getSource() {
        return source;
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
