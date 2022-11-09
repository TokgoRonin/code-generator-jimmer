package io.github.tokgoronin.writers;

import io.github.tokgoronin.interfaces.GeneratorWriter;
import io.github.tokgoronin.mapping.SqlJavaTypeMapping;
import io.github.tokgoronin.table.FieldInfo;
import io.github.tokgoronin.table.TableInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EntityWriter implements GeneratorWriter {
    private static final String DATE_NOW = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

    @Override
    public void generate(String sourcePath, String packagePath, List<TableInfo> tableInfoList, String type) {
        // 先生成目录
        String folderPath = (sourcePath + File.separator + packagePath).replace(".", File.separator) + File.separator + "entity" + File.separator;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 遍历生成entity
        for (TableInfo tableInfo : tableInfoList) {
            String javaName = tableInfo.getJavaName();
            genParentEntity(javaName, folderPath, packagePath, type);
            genEntity(tableInfo, folderPath, packagePath, type);
        }
    }


    public void genEntity(TableInfo tableInfo, String path, String packagePath, String type) {
        List<FieldInfo> fieldInfoList = tableInfo.getFieldInfoList();
        String tableName = tableInfo.getJavaName();
        if ("java".equals(type)) {
            genJavaEntity(tableInfo, path, packagePath, fieldInfoList, tableName);
        } else {
            genKotlinEntity(tableInfo, path, packagePath, fieldInfoList, tableName);
        }
    }

    private static void genJavaEntity(TableInfo tableInfo, String path, String packagePath, List<FieldInfo> fieldInfoList, String tableName) {
        String entityName = tableName + ".java";
        File entityFile = new File(path, entityName);

        try (FileWriter fw = new FileWriter(entityFile); BufferedWriter bw = new BufferedWriter(fw)) {
            writePackageInfoAndImportInfoAndTableComment(tableInfo, packagePath, bw);
            bw.write("public interface " + tableName + " extends " + tableName + "Base {");
            bw.newLine();
            bw.newLine();
            // 循环所有属性
            for (FieldInfo fieldInfo : fieldInfoList) {
                String sqlName = fieldInfo.getSqlName();
                String javaType = fieldInfo.getJavaType();
                String javaName = fieldInfo.getJavaName();
                String key = fieldInfo.getKey();
                String comment = fieldInfo.getComment();
                if (StringUtils.deleteWhitespace(comment).length() > 0) {
                    writeComment(bw, comment);
                }
                if ("PRI".equals(key)) {
                    bw.write("\t@Id");
                    bw.newLine();
                    bw.write("\t@GeneratedValue(strategy = GenerationType.IDENTITY)");
                    bw.newLine();
                    bw.write("\t@Column(name = \"" + sqlName + "\")");
                    bw.newLine();
                    String idType = SqlJavaTypeMapping.ID_TYPE.get(javaType);
                    if (Objects.nonNull(idType)) {
                        bw.write("\t" + idType + " " + javaName + "();");
                    } else {
                        bw.write("\t" + javaType + " " + javaName + "();");
                    }
                } else {
                    Boolean nullable = fieldInfo.getNullAble();
                    if (nullable) {
                        bw.write("\t@Nullable");
                        bw.newLine();
                    }
                    if (!sqlName.equals(javaName)) {
                        bw.write("\t@Column(name = \"" + sqlName + "\")");
                        bw.newLine();
                    }
                    bw.write("\t" + javaType + " " + javaName + "();");
                }
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void genKotlinEntity(TableInfo tableInfo, String path, String packagePath, List<FieldInfo> fieldInfoList, String tableName) {
        String entityName = tableName + ".kt";
        File entityFile = new File(path, entityName);

        try (FileWriter fw = new FileWriter(entityFile); BufferedWriter bw = new BufferedWriter(fw)) {
            writePackageInfoAndImportInfoAndTableComment(tableInfo, packagePath, bw);
            bw.write("interface " + tableName + " : " + tableName + "Base {");
            bw.newLine();
            bw.newLine();
            // 循环所有属性
            for (FieldInfo fieldInfo : fieldInfoList) {
                String sqlName = fieldInfo.getSqlName();
                String fieldType = fieldInfo.getJavaType();
                String fieldName = fieldInfo.getJavaName();
                String key = fieldInfo.getKey();
                String comment = fieldInfo.getComment();
                if (StringUtils.deleteWhitespace(comment).length() > 0) {
                    writeComment(bw, comment);
                }
                // 主键字段
                if ("PRI".equals(key)) {
                    bw.write("\t@Id");
                    bw.newLine();
                    bw.write("\t@GeneratedValue(strategy = GenerationType.IDENTITY)");
                    bw.newLine();
                    bw.write("\t@Column(name = \"" + sqlName + "\")");
                    bw.newLine();
                    String idType = SqlJavaTypeMapping.ID_TYPE.get(fieldType);
                    if (idType != null) {
                        bw.write("\tval " + fieldName + ": " + idType);
                    } else {
                        bw.write("\tval " + fieldName + ": " + fieldType);
                    }
                } else {
                    // 其他字段
                    if (!sqlName.equals(fieldName)) {
                        bw.write("\t@Column(name = \"" + sqlName + "\")");
                        bw.newLine();
                    }
                    Boolean nullable = fieldInfo.getNullAble();
                    if (nullable) {
                        bw.write("val " + fieldName + ": " + fieldType + "?");
                    } else {
                        bw.write("val " + fieldName + ": " + fieldType + "");
                    }
                }
                bw.newLine();
                bw.newLine();
            }

            bw.write("}");
            bw.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void genParentEntity(String name, String path, String packagePath, String type) {
        String entityName;
        if ("java".equals(type)) {
            entityName = name + "Base.kt";
        } else {
            entityName = name + "Base.java";
        }
        File entityFile = new File(path, entityName);
        // 不覆盖parent
        if (entityFile.exists()) {
            return;
        }
        try (FileWriter fw = new FileWriter(entityFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(String.format("package %s.entity;", packagePath));
            bw.newLine();
            bw.newLine();
            bw.write("import org.babyfish.jimmer.sql.MappedSuperclass;");
            bw.newLine();
            bw.newLine();
            writeComment(bw, "自动生成的base接口，用于写关联关系，每次生成不会覆盖此文件。");
            bw.write("@MappedSuperclass");
            bw.newLine();
            if ("java".equals(type)) {
                bw.write("public interface " + name + "Base {");
            } else {
                bw.write("interface " + name + "Base {");
            }
            bw.newLine();
            bw.write("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writePackageInfoAndImportInfoAndTableComment(TableInfo tableInfo, String packagePath, BufferedWriter bw) throws IOException {
        // 包名
        bw.write(String.format("package %s.entity;", packagePath));

        // 空行
        bw.newLine();
        bw.newLine();

        // 导包
        bw.write("import org.babyfish.jimmer.sql.*;");
        bw.newLine();
        if (tableInfo.getHaveBigDecimal()) {
            bw.write("import java.math.BigDecimal;");
            bw.newLine();
        }
        if (tableInfo.getHaveLocalDate()) {
            bw.write("import java.time.LocalDate;");
            bw.newLine();
        }
        if (tableInfo.getHaveLocalTime()) {
            bw.write("import java.time.LocalTime;");
            bw.newLine();
        }
        if (tableInfo.getHaveLocalDateTime()) {
            bw.write("import java.time.LocalDateTime;");
            bw.newLine();
        }

        // 空行
        bw.newLine();
        bw.newLine();

        // 表注释
        String tableComment = tableInfo.getComment();
        if (Objects.nonNull(tableComment) && tableComment.length() > 0) {
            bw.write("/**");
            bw.newLine();
            bw.write(" * " + tableComment);
            bw.newLine();
            bw.write(" */");
            bw.newLine();
        }

        // jimmer实体接口的注解
        bw.write("@Entity");
        bw.newLine();
        bw.write("@Table(name = \"" + tableInfo.getSqlName() + "\")");
        bw.newLine();
    }

    private static void writeComment(BufferedWriter bw, String comment) throws IOException {
        bw.write("/**");
        bw.newLine();
        bw.write(" * ");
        bw.newLine();
        bw.write(" * " + "<p>");
        bw.newLine();
        bw.write(" * " + comment);
        bw.newLine();
        bw.write(" * " + "</p>");
        bw.newLine();
        bw.write(" * ");
        bw.newLine();
        bw.write(" * " + "@Date " + DATE_NOW);
        bw.newLine();
        bw.write(" */");
        bw.newLine();
    }
}
