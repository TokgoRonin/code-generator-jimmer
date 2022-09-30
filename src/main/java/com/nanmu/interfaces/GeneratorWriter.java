package com.nanmu.interfaces;

import com.nanmu.table.TableInfo;

import java.util.List;

public interface GeneratorWriter {
    void generate(String sourcePath, String packagePath, List<TableInfo> tableInfoList);
}
