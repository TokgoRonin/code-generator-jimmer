package com.nanmu.writers;

import com.nanmu.mapping.SqlJavaTypeMapping;
import com.nanmu.table.FieldInfo;
import com.nanmu.table.TableInfo;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class EntityWriter {

    public static void genEntity(TableInfo tableInfo, String sourcePath, String packagePath) {
        List<FieldInfo> fieldInfoList = tableInfo.getFieldInfoList();
        String folderPath = (sourcePath + File.separator + packagePath).replace(".", File.separator);
        String jvaName = tableInfo.getJavaName();
        String entityName = jvaName + ".java";
        File entityFile = new File(folderPath, entityName);

        try (FileWriter fw = new FileWriter(entityFile); BufferedWriter bw = new BufferedWriter(fw)) {
            // 包名
            bw.write("package " + packagePath + ";");

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
            Set<Boolean> nullableSet = fieldInfoList.stream().map(FieldInfo::getNullAble).collect(Collectors.toSet());
            if (nullableSet.contains(Boolean.TRUE)) {
                bw.write("import org.jetbrains.annotations.Nullable;");
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
            bw.write("public interface " + jvaName + " extends " + jvaName + "Base {");
            bw.newLine();
            bw.newLine();
            // 循环所有属性
            for (FieldInfo fieldInfo : fieldInfoList) {
                String sqlName = fieldInfo.getSqlName();
                String javaType = fieldInfo.getJavaType();
                String javaName = fieldInfo.getJavaName();
                if (sqlName.equals("id")) {
                    bw.write("\t@Id");
                    bw.newLine();
                    bw.write("\t@GeneratedValue(strategy = GenerationType.IDENTITY)");
                    bw.newLine();
                    bw.write("\t@Column(name = \"" + sqlName + "\")");
                    bw.newLine();
                    String idType = SqlJavaTypeMapping.ID_TYPE.get(javaType);
                    bw.write("\t" + idType + " " + javaName + "();");
                    bw.newLine();
                    bw.newLine();
                }else {
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
                    bw.newLine();
                    bw.newLine();
                }
            }

            bw.write("}");
            bw.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void genParentEntity(String name, String sourcePath, String packagePath) {
        String folderPath = (sourcePath + File.separator + packagePath).replace(".", File.separator);
        String entityName = name + "Base.java";
        File entityFile = new File(folderPath, entityName);
        // 不覆盖parent
        if (entityFile.exists()) {
            return;
        }
        try (FileWriter fw = new FileWriter(entityFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("package " + packagePath + ";");
            bw.newLine();
            bw.newLine();
            bw.write("import org.babyfish.jimmer.sql.MappedSuperclass;");
            bw.newLine();
            bw.newLine();
            bw.write("@MappedSuperclass");
            bw.newLine();
            bw.write("public interface " + name + "Base {");
            bw.newLine();
            bw.write("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}