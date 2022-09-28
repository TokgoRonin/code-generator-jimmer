package com.nanmu;

import com.nanmu.mapping.SqlJavaTypeMapping;
import com.nanmu.table.FieldInfo;
import com.nanmu.table.TableInfo;
import com.nanmu.utils.CustomStringUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Mojo(name = "codeGen")
public class CodeGenMojo extends AbstractMojo {
    public static final String SHOW_TABLE_STATUS = "show table status";
    public static final String SHOW_TABLE_FIELDS = "show full fields from %s";

    @Parameter(property = "driver")
    private String driver;
    @Parameter(property = "url")
    private String url;
    @Parameter(property = "username")
    private String username;
    @Parameter(property = "password")
    private String password;
    @Parameter(property = "tables")
    private List<String> tables;
    @Parameter(property = "tablePreFix")
    private String tablePrefix;


    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info(driver);
        this.getLog().info(url);
        this.getLog().info(username);
        this.getLog().info(password);
        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);
            List<TableInfo> tableInfoList = getTableInfoList(connection);
            tableInfoList.forEach(tableInfo -> {
                List<FieldInfo> fieldInfoList = getFieldInfoList(connection, tableInfo.getSqlName());
                tableInfo.setFieldInfoList(fieldInfoList);

                Set<String> fieldTypeSet = fieldInfoList.stream().map(FieldInfo::getJavaType)
                                                        .collect(Collectors.toSet());
                tableInfo.setHaveLocalDate(fieldTypeSet.contains("LocalDate"));
                tableInfo.setHaveLocalTime(fieldTypeSet.contains("LocalTime"));
                tableInfo.setHaveLocalDateTime(fieldTypeSet.contains("LocalDateTime"));
                tableInfo.setHaveBigDecimal(fieldTypeSet.contains("BigDecimal"));
                this.getLog().info("table info : " + tableInfo);
            });
        } catch (SQLException e) {
            this.getLog().error("读取表结构失败：" + e);
        } catch (ClassNotFoundException e) {
            this.getLog().error("获取数据库驱动失败:" + e);
        }
    }

    private List<TableInfo> getTableInfoList(Connection connection) {
        List<TableInfo> tableInfoList = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder(SHOW_TABLE_STATUS);
        if (Objects.nonNull(tables) && !tables.isEmpty()) {
            String tableNames = tables.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", "));
            sqlBuilder.append(" WHERE Name in ( ").append(tableNames).append(" )");
        }
        this.getLog().info("query table status sql : " + sqlBuilder);
        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString()); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tableName = rs.getString("name");
                // 生成的实体接口过滤表前缀
                String javaName;
                if (Objects.nonNull(tablePrefix) && tablePrefix.length() > 0) {
                    javaName = CustomStringUtil.toCamelCase(tableName.replaceFirst(tablePrefix, ""));
                } else {
                    javaName = CustomStringUtil.toCamelCase(tableName);
                }
                String tableComment = rs.getString("comment");

                TableInfo tableInfo = new TableInfo();
                tableInfo.setSqlName(tableName);
                tableInfo.setJavaName(javaName);
                tableInfo.setComment(tableComment);

                tableInfoList.add(tableInfo);
            }
        } catch (SQLException e) {
            this.getLog().error("读取表结构失败：" + e);
        }

        return tableInfoList;
    }

    public List<FieldInfo> getFieldInfoList(Connection connection, String tableName) {
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        String sql = String.format(SHOW_TABLE_FIELDS, tableName);
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String fieldName = rs.getString("field");
                String javaName = CustomStringUtil.toCamelCase(fieldName);
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
        } catch (SQLException e) {
            this.getLog().error("读取表字段失败：" + e);
        }
        return fieldInfoList;
    }

}
