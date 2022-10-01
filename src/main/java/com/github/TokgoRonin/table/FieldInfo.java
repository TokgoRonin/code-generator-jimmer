package com.github.TokgoRonin.table;

public class FieldInfo {
    private String sqlType;
    private String javaType;
    private String sqlName;
    private String javaName;
    private String comment;

    private Boolean nullAble;

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

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

    public Boolean getNullAble() {
        return nullAble;
    }

    public void setNullAble(Boolean nullAble) {
        this.nullAble = nullAble;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "sqlType='" + sqlType + '\'' +
                ", javaType='" + javaType + '\'' +
                ", sqlName='" + sqlName + '\'' +
                ", javaName='" + javaName + '\'' +
                ", comment='" + comment + '\'' +
                ", nullAble=" + nullAble +
                '}';
    }
}