package com.github.TokgoRonin.interfaces;

import com.github.TokgoRonin.table.TableInfo;

import java.util.List;

public interface GeneratorWriter {
    void generate(String sourcePath, String packagePath, List<TableInfo> tableInfoList);
}
