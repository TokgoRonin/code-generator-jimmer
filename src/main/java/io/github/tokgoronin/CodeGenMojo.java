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
    public static final String SHOW_TABLE_STATUS = "show table status";
    public static final String SHOW_TABLE_FIELDS = "show full fields from %s";

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

    public void execute() throws MojoExecutionException, MojoFailureException {
        String url = jdbcConfig.getUrl();
        String username = jdbcConfig.getUsername();
        String password = jdbcConfig.getPassword();
        String driver = jdbcConfig.getDriver();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            this.getLog().error("获取数据库驱动失败:" + e);
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);) {
            if (driver.toLowerCase().contains("mysql")) {
                generator = new MysqlGenerator();
                // 读取表信息
                List<TableInfo> tableInfoList = generator.getTableInfoList(connection, tables, tablePrefix);
                // 生成entity
                entityWriter.generate(sourcePath, packagePath, tableInfoList);
                // 生成dao
                if (daos) {
                    daoWriter.generate(sourcePath, packagePath, tableInfoList);
                }
            } else {
                this.getLog().error("暂不支持该数据库类型。");
            }
        } catch (SQLException e) {
            this.getLog().error("读取表结构失败：" + e);
        }
    }

}
