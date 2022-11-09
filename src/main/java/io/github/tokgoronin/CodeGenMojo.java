package io.github.tokgoronin;

import io.github.tokgoronin.dialects.MysqlGenerator;
import io.github.tokgoronin.config.JdbcConfig;
import io.github.tokgoronin.interfaces.Generator;
import io.github.tokgoronin.table.TableInfo;
import io.github.tokgoronin.writers.DaoWriter;
import io.github.tokgoronin.writers.EntityWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.sql.*;
import java.util.*;

@Mojo(name = "codeGen")
public class CodeGenMojo extends AbstractMojo {
    private static final String TYPE_JAVA = "java";
    private static final String TYPE_KOTLIN = "kotlin";

    private Generator generator;
    private final EntityWriter entityWriter = new EntityWriter();
    private final DaoWriter daoWriter = new DaoWriter();

    @Parameter(property = "tables")
    private LinkedList<String> tables;
    @Parameter(property = "tablePreFix")
    private String tablePrefix;
    @Parameter(property = "packagePath")
    private String packagePath;
    @Parameter(property = "sourcePath")
    private String sourcePath;
    @Parameter
    private JdbcConfig jdbcConfig;
    @Parameter
    private boolean daos;
    private String codeType;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!TYPE_JAVA.equals(codeType) && !TYPE_KOTLIN.equals(codeType)) {
            this.getLog().error("codeType 必须是 java 或 kotlin 。");
            throw new MojoFailureException("codeType 必须是 java 或 kotlin 。");
        }

        String url = jdbcConfig.getUrl();
        String username = jdbcConfig.getUsername();
        String password = jdbcConfig.getPassword();
        String driver = jdbcConfig.getDriver();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            this.getLog().error("获取数据库驱动失败:" + e);
        }
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (driver.toLowerCase().contains("mysql")) {
                generator = new MysqlGenerator();
                // 读取表信息
                List<TableInfo> tableInfoList = generator.getTableInfoList(connection, tables, tablePrefix);
                // 生成entity
                entityWriter.generate(sourcePath, packagePath, tableInfoList, codeType);
                // 生成dao
                if (daos) {
                    daoWriter.generate(sourcePath, packagePath, tableInfoList, codeType);
                }
            } else {
                this.getLog().error("暂不支持该数据库类型。");
                throw new MojoFailureException("暂不支持该数据库类型。");
            }
        } catch (SQLException e) {
            this.getLog().error("读取表结构失败：" + e.getCause());
            throw new MojoFailureException("读取表结构失败。");
        }
    }

}
