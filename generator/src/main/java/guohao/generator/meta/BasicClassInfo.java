package guohao.generator.meta;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 可以根据{@link BasicClassInfo#getSource()}和{@link BasicClassInfo#getFullClassName()}唯一确定一条记录
 */
public enum BasicClassInfo implements ClassInfo{
    GUAVA_LIST("java.util.List", "com.google.common.collect.Lists", "Lists.newArrayList()", Source.GUAVA),
    GUAVA_MAP("java.util.Map", "com.google.common.collect.Maps", "Maps.newHashMap()", Source.GUAVA),
    GUAVA_SET("java.util.Set", "com.google.common.collect.Sets", "Sets.newHashSet()", Source.GUAVA),

    JAVA_LIST("java.util.List", "java.util.ArrayList", "new ArrayList<>()", Source.JAVA),
    JAVA_MAP("java.util.Map", "java.util.HashMap", "new HashMap<>()", Source.JAVA),
    JAVA_SET("java.util.Set", "java.util.HashSet", "new HashSet<>()", Source.JAVA),

    // 基础类型以及包装类不需要导入包
    BOOLEAN("boolean", "", "false", Source.JAVA),
    BOOLEAN_BOX("java.lang.Boolean", "", "false", Source.JAVA),
    INTEGER("int", "", "0", Source.JAVA),
    INTEGER_BOX("java.lang.Integer", "", "0", Source.JAVA),
    BYTE("byte", "", "(byte)0", Source.JAVA),
    BYTE_BOX("java.lang.Byte", "", "(byte)0", Source.JAVA),
    STRING("java.lang.String", "", "\"\"", Source.JAVA),
    LONG("long", "", "0L", Source.JAVA),
    LONG_BOX("java.lang.Long", "", "0L", Source.JAVA),
    SHORT("short", "", "(short)0", Source.JAVA),
    SHORT_BOX("java.lang.Short", "", "(short)0", Source.JAVA),
    FLOAT("float", "", "0F", Source.JAVA),
    FLOAT_BOX("java.lang.Float", "", "0F", Source.JAVA),
    DOUBLE("double", "", "0.0D", Source.JAVA),
    DOUBLE_BOX("java.lang.Double", "", "0.0D", Source.JAVA),
    CHAR("char", "", "''", Source.JAVA),
    CHAR_BOX("java.lang.Character", "", "''", Source.JAVA),

    DATE("java.util.Date", "java.util.Date", "new Date()", Source.JAVA),
    BIG_DECIMAL("java.math.BigDecimal", "java.math.BigDecimal", "new BigDecimal(\"0\")", Source.JAVA),
    LOCAL_DATETIME("java.time.LocalDateTime", "java.time.LocalDateTime", "LocalDateTime.now()", Source.JAVA),
    LOCAL_DATE("java.time.LocalDate", "java.time.LocalDate", "LocalDate.now()", Source.JAVA),

    SQL_DATE("java.sql.Date", "java.sql.Date", "new Date(new java.util.Date().getTime())", Source.JAVA),
    SQL_TIMESTAMP("java.sql.Timestamp", "java.sql.Timestamp", "new Timestamp(new java.util.Date().getTime())", Source.JAVA),
    NULL("null", "", "null", Source.JAVA),
    ;

    private final String fullClassName;
    private final String packageName;
    private final String instance;
    private final Source source;

    BasicClassInfo(String fullClassName, String packageName, String instance, Source source) {
        this.fullClassName = fullClassName;
        this.packageName = packageName;
        this.instance = instance;
        this.source = source;
    }

    private static Map<String, BasicClassInfo> idMap;
    private static Map<String, List<BasicClassInfo>> sortNameMap;

    static {
        idMap = Arrays.stream(BasicClassInfo.values())
                .collect(Collectors.toMap(basicClassInfo -> generateId(basicClassInfo.getFullClassName(), basicClassInfo.getSource()), Function.identity()));
        sortNameMap = Arrays.stream(BasicClassInfo.values())
                .collect(Collectors.groupingBy(BasicClassInfo::getFullClassName));
    }

    /**
     * 可以根据{@link BasicClassInfo#getSource()}和{@link BasicClassInfo#getFullClassName()}唯一确定一条记录
     *
     * @param sortName
     * @param source
     * @return
     */
    @NotNull
    public static Optional<BasicClassInfo> get(String sortName, Source source) {
        return Optional.ofNullable(idMap.get(generateId(sortName, source)));
    }

    @NotNull
    public static List<BasicClassInfo> get(String fullClassName) {
        return sortNameMap.getOrDefault(fullClassName, Collections.emptyList());
    }

    /**
     * 生成唯一标识
     */
    private static String generateId(String fullClassName, Source source) {
        return fullClassName + source.name();
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