package guohao.generator;

import java.util.ResourceBundle;

/**
 * bundle资源管理器
 */
public final class BundleManager {
    private BundleManager() {
    }

    /**
     * The {@link ResourceBundle} path.
     */
    private static final String GENERATOR_BUNDLE_NAME = "GeneratorBundle";

    private static final ResourceBundle GENERATOR = ResourceBundle.getBundle(GENERATOR_BUNDLE_NAME);

    public static String getFamilyName(String name, Object... args) {
        return String.format(GENERATOR.getString(name), args);
    }

    public static String getGeneratorBundle(String name, Object... args) {
        return String.format(GENERATOR.getString(name), args);
    }
}
