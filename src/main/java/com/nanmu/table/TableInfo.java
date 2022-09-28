package com.nanmu.table;

import java.math.BigDecimal;
import java.util.List;

public class TableInfo {
    private String sqlName;
    private String javaName;
    private String comment;
    private List<FieldInfo> fieldInfoList;
    private Boolean haveDate;
    private Boolean haveDateTime;
    private Boolean haveBigDecimal;

    public String getSqlName() {
        return sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }

    public Boolean getHaveDate() {
        return haveDate;
    }

    public void setHaveDate(Boolean haveDate) {
        this.haveDate = haveDate;
    }

    public Boolean getHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(Boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public Boolean getHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(Boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "sqlName='" + sqlName + '\'' +
                ", javaName='" + javaName + '\'' +
                ", comment='" + comment + '\'' +
                ", fieldInfoList=" + fieldInfoList +
                ", haveDate=" + haveDate +
                ", haveDateTime=" + haveDateTime +
                ", haveBigDecimal=" + haveBigDecimal +
                '}';
    }
}
