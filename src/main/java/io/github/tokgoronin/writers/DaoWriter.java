package io.github.tokgoronin.writers;

import io.github.tokgoronin.interfaces.GeneratorWriter;
import io.github.tokgoronin.table.TableInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DaoWriter implements GeneratorWriter {
    @Override
    public void generate(String sourcePath, String packagePath, List<TableInfo> tableInfoList) {
        // 先生成目录
        String folderPath = (sourcePath + File.separator + packagePath).replace(".", File.separator) + File.separator + "dao" + File.separator;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        generateBaseDao(folderPath, packagePath);
        // 遍历生成dao
        for (TableInfo tableInfo : tableInfoList) {
            generateDao(folderPath, packagePath, tableInfo);
        }
    }

    private void generateBaseDao(String path, String packagePath) {
        File dao = new File(path, "BaseDao.java");
        try (FileWriter fw = new FileWriter(dao); BufferedWriter bw = new BufferedWriter(fw)) {
            String packageName = String.format("package %s;", packagePath + ".dao");
            bw.write(packageName);
            bw.newLine();
            bw.newLine();

            bw.write("import org.babyfish.jimmer.sql.JSqlClient;\n" +
                    "import org.babyfish.jimmer.sql.ast.mutation.BatchSaveResult;\n" +
                    "import org.babyfish.jimmer.sql.ast.mutation.DeleteResult;\n" +
                    "import org.babyfish.jimmer.sql.ast.mutation.SaveMode;\n" +
                    "import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;\n" +
                    "import org.babyfish.jimmer.sql.ast.table.Table;\n" +
                    "import org.babyfish.jimmer.sql.fetcher.Fetcher;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "\n" +
                    "import javax.annotation.Resource;\n" +
                    "import java.util.Collection;\n" +
                    "import java.util.List;\n" +
                    "import java.util.Optional;\n");
            bw.newLine();

            bw.write("@Repository\n" +
                    "public abstract class BaseDao<T extends Table<?>, E> {\n" +
                    "\n" +
                    "    @Resource\n" +
                    "    private JSqlClient sqlClient;\n" +
                    "\n" +
                    "    public E save(E entity) {\n" +
                    "        return sqlClient.getEntities().save(entity).getModifiedEntity();\n" +
                    "    }\n" +
                    "\n" +
                    "    public BatchSaveResult<E> batchSave(Collection<E> entities) {\n" +
                    "        return sqlClient.getEntities().batchSave(entities);\n" +
                    "    }\n" +
                    "\n" +
                    "    public SimpleSaveResult<E> insert(E entity) {\n" +
                    "        return sqlClient.getEntities().saveCommand(entity).configure(it -> it.setMode(SaveMode.INSERT_ONLY)).execute();\n" +
                    "    }\n" +
                    "\n" +
                    "    public BatchSaveResult<E> batchInsert(Collection<E> entities) {\n" +
                    "        return sqlClient.getEntities().batchSaveCommand(entities).configure(it -> it.setMode(SaveMode.INSERT_ONLY)).execute();\n" +
                    "    }\n" +
                    "\n" +
                    "    public DeleteResult deleteById(Class<T> entityTableClazz, Object id) {\n" +
                    "        return sqlClient.getEntities().delete(entityTableClazz, id);\n" +
                    "    }\n" +
                    "\n" +
                    "    public DeleteResult batchDelete(Class<T> entityClazz, Collection<Object> ids) {\n" +
                    "        return sqlClient.getEntities().batchDelete(entityClazz, ids);\n" +
                    "    }\n" +
                    "\n" +
                    "    public E update(E entity) {\n" +
                    "        return sqlClient.getEntities().saveCommand(entity).configure(it -> it.setMode(SaveMode.UPDATE_ONLY)).execute().getModifiedEntity();\n" +
                    "    }\n" +
                    "\n" +
                    "    public Optional<E> findById(Class<E> entityClazz, Object id) {\n" +
                    "        return Optional.ofNullable(sqlClient.getEntities().findById(entityClazz, id));\n" +
                    "    }\n" +
                    "\n" +
                    "    public Optional<E> findById(Fetcher<E> fetcher, Object id) {\n" +
                    "        return Optional.ofNullable(sqlClient.getEntities().findById(fetcher, id));\n" +
                    "    }\n" +
                    "\n" +
                    "    public List<E> findAll(Class<E> entityClazz) {\n" +
                    "        return sqlClient.getEntities().findAll(entityClazz);\n" +
                    "    }\n" +
                    "\n" +
                    "    public abstract List<E> findAllByPage(Class<T> entityTableClazz, int page, int size);\n" +
                    "\n" +
                    "}\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateDao(String path, String packagePath, TableInfo tableInfo) {
        String entityName = tableInfo.getJavaName();
        String daoName = entityName + "Dao.java";
        File dao = new File(path, daoName);
        try (FileWriter fw = new FileWriter(dao); BufferedWriter bw = new BufferedWriter(fw)) {
            String packageName = String.format("package %s;", packagePath + ".dao");
            bw.write(packageName);
            bw.newLine();
            bw.newLine();

            bw.write(String.format("import %s.entity.%s;", packagePath, entityName));
            bw.newLine();
            bw.write(String.format("import %s.entity.%sTable;", packagePath, entityName));
            bw.newLine();
            bw.write("import org.babyfish.jimmer.sql.JSqlClient;");
            bw.newLine();
            bw.write("import org.babyfish.jimmer.sql.ast.query.selectable.RootSelectable;");
            bw.newLine();
            bw.write("import org.springframework.stereotype.Repository;");
            bw.newLine();
            bw.newLine();
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.newLine();
            bw.write("@Repository");
            bw.newLine();
            bw.write(String.format("public class %sDao extends BaseDao<%sTable, %s> {", entityName, entityName, entityName));
            bw.newLine();
            bw.newLine();
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate JSqlClient sqlClient;");
            bw.newLine();
            bw.newLine();
            bw.write("\t@Override");
            bw.newLine();
            bw.write(String.format("\tpublic List<%s> findAllByPage(Class<%sTable> entityTableClazz, int page, int size) {", entityName, entityName));
            bw.newLine();
            bw.write("\t\treturn sqlClient.createQuery(entityTableClazz, RootSelectable::select).limit(size, page * size).execute();");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();
            bw.write("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
