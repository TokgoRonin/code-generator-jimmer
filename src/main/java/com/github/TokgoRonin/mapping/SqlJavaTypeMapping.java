package com.github.TokgoRonin.mapping;

import java.util.HashMap;
import java.util.Map;

public class SqlJavaTypeMapping {
    public static final Map<String, String> TYPE = new HashMap<>();

    public static final Map<String, String> ID_TYPE = new HashMap<>();

    static {
        // 字符串类型
        TYPE.put("char", "String");
        TYPE.put("varchar", "String");
        TYPE.put("tinytext", "String");
        TYPE.put("mediumtext", "String");
        TYPE.put("text", "String");
        TYPE.put("longtext", "String");
        // 日期或时间类型
        TYPE.put("date", "LocalDate");
        TYPE.put("time", "LocalTime");
        TYPE.put("datetime", "LocalDateTime");
        TYPE.put("timestamp", "LocalDateTime");
        TYPE.put("year", "LocalDate");
        // 数字
        TYPE.put("tinyint", "Integer");
        TYPE.put("smallint", "Integer");
        TYPE.put("mediumint", "Integer");
        TYPE.put("int", "Integer");
        TYPE.put("bigint", "Long");
        TYPE.put("float", "Float");
        TYPE.put("double", "Double");
        TYPE.put("decimal", "BigDecimal");
        // 二进制类型
        TYPE.put("tityblob", "byte[]");
        TYPE.put("blob", "byte[]");
        TYPE.put("mediumblob", "byte[]");
        TYPE.put("longblob", "byte[]");

        ID_TYPE.put("Integer", "int");
        ID_TYPE.put("Long", "long");
        ID_TYPE.put("Float", "float");
        ID_TYPE.put("Double", "double");
    }

}
