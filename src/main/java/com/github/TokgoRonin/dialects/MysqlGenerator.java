package com.github.TokgoRonin.dialects;

import com.github.TokgoRonin.interfaces.Generator;
import com.github.TokgoRonin.mapping.SqlJavaTypeMapping;
import com.github.TokgoRonin.table.FieldInfo;
import com.github.TokgoRonin.table.TableInfo;
import com.github.TokgoRonin.utils.CustomStringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MysqlGenerator implements Generator {

    public static final String SHOW_TABLE_STATUS = "show table status";
    public static final String SHOW_TABLE_FIELDS = "show full fields from %s";

    @Override
    public List<TableInfo> getTableInfoList(Connection connection, LinkedList<String> tables, String tablePrefix) throws SQLException {
        List<TableInfo> tableInfoList = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder(SHOW_TABLE_STATUS);
        if (Objects.nonNull(tables) && !tables.isEmpty()) {
            String tableNames = tables.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", "));
            sqlBuilder.append(" WHERE Name in ( ").append(tableNames).append(" )");
        }
        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString()); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // 获取表名
                String tableName = rs.getString("name");
                // 获取该表所有字段信息
                List<FieldInfo> fieldInfoList = getFieldInfoList(connection, tableName);
                // 生成的实体接口过滤表前缀
                String javaName;
                if (Objects.nonNull(tablePrefix) && tablePrefix.trim().length() > 0) {
                    javaName = CustomStringUtil.toCamelCase(tableName.replaceFirst(tablePrefix, ""), true);
                } else {
                    javaName = CustomStringUtil.toCamelCase(tableName, true);
                }
                String tableComment = rs.getString("comment");

                TableInfo tableInfo = new TableInfo();
                tableInfo.setSqlName(tableName);
                tableInfo.setJavaName(javaName);
                tableInfo.setComment(tableComment);
                // 这四个属性用于判断生成的entity中是否要导入对应的包
                tableInfo.setFieldInfoList(fieldInfoList);
                Set<String> fieldTypeSet = fieldInfoList.stream().map(FieldInfo::getJavaType)
                                                        .collect(Collectors.toSet());
                tableInfo.setHaveLocalDate(fieldTypeSet.contains("LocalDate"));
                tableInfo.setHaveLocalTime(fieldTypeSet.contains("LocalTime"));
                tableInfo.setHaveLocalDateTime(fieldTypeSet.contains("LocalDateTime"));
                tableInfo.setHaveBigDecimal(fieldTypeSet.contains("BigDecimal"));

                tableInfoList.add(tableInfo);
            }
        }

        return tableInfoList;
    }

    @Override
    public List<FieldInfo> getFieldInfoList(Connection connection, String tableName) throws SQLException {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        String sql = String.format(SHOW_TABLE_FIELDS, tableName);
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String fieldName = rs.getString("field");
                String javaName = CustomStringUtil.toCamelCase(fieldName, false);
                String type = rs.getString("type");
                if (type.indexOf("(") > 0) {
                    type = type.substring(0, type.indexOf("("));
                }
                String javaType = SqlJavaTypeMapping.TYPE.get(type);
                boolean nullAble = rs.getBoolean("null");
                String comment = rs.getString("comment");

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setSqlType(type);
                fieldInfo.setJavaType(javaType);
                fieldInfo.setSqlName(fieldName);
                fieldInfo.setJavaName(javaName);
                fieldInfo.setComment(comment);
                fieldInfo.setNullAble(nullAble);

                fieldInfoList.add(fieldInfo);
            }
        }
        return fieldInfoList;
    }
}
