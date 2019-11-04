package com.cplusjuice.anorm.util;

import com.cplusjuice.anorm.exception.JavaJDBCTypeConversionException;

import java.sql.ResultSet;

public class JavaJDBCTypesConverter {

    private JavaJDBCType javaJDBCType;

    public JavaJDBCTypesConverter(int JDBCType) throws JavaJDBCTypeConversionException {
        JavaJDBCType[] types = JavaJDBCType.class.getEnumConstants();

        for (JavaJDBCType type : types) {
            if (type.getJDBCType() == JDBCType) {
                javaJDBCType = type;
                return;
            }
        }

        throw JavaJDBCTypeConversionException.unknownType(JDBCType);
    }

    public JavaJDBCTypesConverter(Class javaType) throws JavaJDBCTypeConversionException {
        JavaJDBCType[] types = JavaJDBCType.class.getEnumConstants();

        for (JavaJDBCType type : types) {
            if (type.getJavaType().equals(javaType)) {
                javaJDBCType = type;
                return;
            }
        }

        throw JavaJDBCTypeConversionException.unknownType(javaType);
    }

    public Class asJavaType() {
        return javaJDBCType.getJavaType();
    }

    public int asJDBCType() {
        return javaJDBCType.getJDBCType();
    }

    public Object invokeGetValue(ResultSet resultSet, String name) {
        return javaJDBCType.getValue(resultSet, name);
    }

    public String getJDBCName() {
        return javaJDBCType.getJDBCName();
    }
}
