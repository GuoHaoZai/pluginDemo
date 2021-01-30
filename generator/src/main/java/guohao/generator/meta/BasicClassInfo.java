package guohao.generator.meta;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可以根据{@link BasicClassInfo#getLibrary()}和{@link BasicClassInfo#getFullClassName()}唯一确定一条记录
 */
public enum BasicClassInfo implements ClassInfo{
    GUAVA_LIST("java.util.List", "com.google.common.collect.Lists", "Lists.newArrayList()", Library.GUAVA),
    GUAVA_MAP("java.util.Map", "com.google.common.collect.Maps", "Maps.newHashMap()", Library.GUAVA),
    GUAVA_SET("java.util.Set", "com.google.common.collect.Sets", "Sets.newHashSet()", Library.GUAVA),

    JAVA_LIST("java.util.List", "java.util.ArrayList", "new ArrayList<>()", Library.JAVA),
    JAVA_MAP("java.util.Map", "java.util.HashMap", "new HashMap<>()", Library.JAVA),
    JAVA_SET("java.util.Set", "java.util.HashSet", "new HashSet<>()", Library.JAVA),

    // 基础类型以及包装类不需要导入包
    BOOLEAN("boolean", "", "false", Library.JAVA),
    BOOLEAN_BOX("java.lang.Boolean", "", "false", Library.JAVA),
    INTEGER("int", "", "0", Library.JAVA),
    INTEGER_BOX("java.lang.Integer", "", "0", Library.JAVA),
    BYTE("byte", "", "(byte)0", Library.JAVA),
    BYTE_BOX("java.lang.Byte", "", "(byte)0", Library.JAVA),
    STRING("java.lang.String", "", "\"\"", Library.JAVA),
    LONG("long", "", "0L", Library.JAVA),
    LONG_BOX("java.lang.Long", "", "0L", Library.JAVA),
    SHORT("short", "", "(short)0", Library.JAVA),
    SHORT_BOX("java.lang.Short", "", "(short)0", Library.JAVA),
    FLOAT("float", "", "0F", Library.JAVA),
    FLOAT_BOX("java.lang.Float", "", "0F", Library.JAVA),
    DOUBLE("double", "", "0.0D", Library.JAVA),
    DOUBLE_BOX("java.lang.Double", "", "0.0D", Library.JAVA),
    CHAR("char", "", "''", Library.JAVA),
    CHAR_BOX("java.lang.Character", "", "''", Library.JAVA),

    DATE("java.util.Date", "java.util.Date", "new Date()", Library.JAVA),
    BIG_DECIMAL("java.math.BigDecimal", "java.math.BigDecimal", "new BigDecimal(\"0\")", Library.JAVA),
    LOCAL_DATETIME("java.time.LocalDateTime", "java.time.LocalDateTime", "LocalDateTime.now()", Library.JAVA),
    LOCAL_DATE("java.time.LocalDate", "java.time.LocalDate", "LocalDate.now()", Library.JAVA),

    SQL_DATE("java.sql.Date", "java.sql.Date", "new Date(new java.util.Date().getTime())", Library.JAVA),
    SQL_TIMESTAMP("java.sql.Timestamp", "java.sql.Timestamp", "new Timestamp(new java.util.Date().getTime())", Library.JAVA),
    NULL("null", "", "null", Library.JAVA),
    ;

    private final String fullClassName;
    private final String packageName;
    private final String instance;
    private final Library library;

    BasicClassInfo(String fullClassName, String packageName, String instance, Library library) {
        this.fullClassName = fullClassName;
        this.packageName = packageName;
        this.instance = instance;
        this.library = library;
    }

    private static Map<String, BasicClassInfo> idMap;
    private static Map<String, List<BasicClassInfo>> sortNameMap;

    static {
        idMap = Arrays.stream(BasicClassInfo.values())
                .collect(Collectors.toMap(basicClassInfo -> generateId(basicClassInfo.getFullClassName(), basicClassInfo.getLibrary()), Function.identity()));
        sortNameMap = Arrays.stream(BasicClassInfo.values())
                .collect(Collectors.groupingBy(BasicClassInfo::getFullClassName));
    }

    /**
     * 可以根据{@link BasicClassInfo#getLibrary()}和{@link BasicClassInfo#getFullClassName()}唯一确定一条记录
     *
     * @param sortName
     * @param library
     * @return
     */
    @NotNull
    public static Optional<BasicClassInfo> get(String sortName, Library library) {
        return Optional.ofNullable(idMap.get(generateId(sortName, library)));
    }

    @NotNull
    public static List<BasicClassInfo> get(String fullClassName) {
        return sortNameMap.getOrDefault(fullClassName, Collections.emptyList());
    }

    /**
     * 生成唯一标识
     */
    private static String generateId(String fullClassName, Library library) {
        return fullClassName + library.name();
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