package io.github.tokgoronin.interfaces;

import io.github.tokgoronin.table.FieldInfo;
import io.github.tokgoronin.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public interface Generator {
    List<TableInfo> getTableInfoList(Connection connection, LinkedList<String> tables, String tablePrefix) throws SQLException;

    List<FieldInfo> getFieldInfoList(Connection connection, String tableName) throws SQLException;
}
