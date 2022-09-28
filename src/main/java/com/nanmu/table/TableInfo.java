package com.nanmu.table;

import java.util.List;

public class TableInfo {
    private String sqlName;
    private String javaName;
    private String comment;
    private List<FieldInfo> fieldInfoList;
    private Boolean haveLocalDate;
    private Boolean haveLocalTime;
    private Boolean haveLocalDateTime;
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

    public Boolean getHaveLocalDate() {
        return haveLocalDate;
    }

    public void setHaveLocalDate(Boolean haveLocalDate) {
        this.haveLocalDate = haveLocalDate;
    }

    public Boolean getHaveLocalTime() {
        return haveLocalTime;
    }

    public void setHaveLocalTime(Boolean haveLocalTime) {
        this.haveLocalTime = haveLocalTime;
    }

    public Boolean getHaveLocalDateTime() {
        return haveLocalDateTime;
    }

    public void setHaveLocalDateTime(Boolean haveLocalDateTime) {
        this.haveLocalDateTime = haveLocalDateTime;
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
                ", haveLocalDate=" + haveLocalDate +
                ", haveLocalTime=" + haveLocalTime +
                ", haveLocalDateTime=" + haveLocalDateTime +
                ", haveBigDecimal=" + haveBigDecimal +
                '}';
    }
}
