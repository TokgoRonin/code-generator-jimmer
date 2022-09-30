package com.nanmu.interfaces;

import com.nanmu.table.FieldInfo;
import com.nanmu.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public interface Generator {
    List<TableInfo> getTableInfoList(Connection connection, LinkedList<String> tables, String tablePrefix) throws SQLException;

    List<FieldInfo> getFieldInfoList(Connection connection, String tableName) throws SQLException;
}
