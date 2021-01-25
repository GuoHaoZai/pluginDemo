package guohao.common.utils;

import org.jetbrains.annotations.PropertyKey;

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
    private static final String PROJECT_BUNDLE_NAME = "bundles.ProjectBundle";

    private static final String GENERATOR_BUNDLE_NAME = "bundles.GeneratorBundle";

    /**
     * The {@link ResourceBundle} instance.
     */
    private static final ResourceBundle PROJECT = ResourceBundle.getBundle(PROJECT_BUNDLE_NAME);

    private static final ResourceBundle GENERATOR = ResourceBundle.getBundle(GENERATOR_BUNDLE_NAME);

    public static String getProjectBundle(@PropertyKey(resourceBundle = PROJECT_BUNDLE_NAME) String name, Object... args) {
        return String.format(PROJECT.getString(name), args);
    }

    public static String getGeneratorBundle(@PropertyKey(resourceBundle = GENERATOR_BUNDLE_NAME) String name, Object... args) {
        return String.format(GENERATOR.getString(name), args);
    }
}
