package io.github.tokgoronin.interfaces;

import io.github.tokgoronin.table.TableInfo;

import java.util.List;

public interface GeneratorWriter {
    void generate(String sourcePath, String packagePath, List<TableInfo> tableInfoList);
}
