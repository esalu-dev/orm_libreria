package org.example.database;
import java.util.HashMap;
import java.util.Map;

public class SqlTypesMapper {
    private static final Map<String, String> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("int", "INTEGER");
        TYPE_MAP.put("java.lang.String", "VARCHAR");
    }

    public static String getSqlType(String javaType) {
        return TYPE_MAP.getOrDefault(javaType, "TEXT");
    }
}
