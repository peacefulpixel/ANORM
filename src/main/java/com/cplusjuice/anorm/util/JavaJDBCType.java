package com.cplusjuice.anorm.util;

import com.cplusjuice.anorm.exception.InvalidSqlTableException;

import java.sql.*;

public enum JavaJDBCType {

    BOOLEAN(Boolean.class, Types.BOOLEAN, ResultSet::getBoolean, "BOOLEAN"),
    DATE(Date.class, Types.DATE, ResultSet::getDate, "DATE"),
    DOUBLE(Double.class, Types.DOUBLE, ResultSet::getDouble, "DOUBLE"),
    FLOAT(Float.class, Types.FLOAT, ResultSet::getFloat, "FLOAT"),
    INTEGER(Integer.class, Types.INTEGER, ResultSet::getInt, "INTEGER"),
    TIMESTAMP(Timestamp.class, Types.TIMESTAMP, ResultSet::getTimestamp, "TIMESTAMP"),
    STRING(String.class, Types.VARCHAR, ResultSet::getString, "VARCHAR");

    private Class javaType;
    private int JDBCType;
    private JDBCGetter getter;
    private String JDBCName;

    private JavaJDBCType(Class javaType, int JDBCType, JDBCGetter getter, String JDBCName) {
        this.javaType = javaType;
        this.JDBCType = JDBCType;
        this.getter   = getter;
        this.JDBCName = JDBCName;
    }

    public Class getJavaType() {
        return javaType;
    }

    public int getJDBCType() {
        return JDBCType;
    }

    public String getJDBCName() {
        return JDBCName;
    }

    public Object getValue(ResultSet resultSet, String name) {
        try {
            return getter.getValue(resultSet, name);
        } catch (SQLException e) {
            throw InvalidSqlTableException.invalidColumnType(JDBCType);
        }
    }
}
