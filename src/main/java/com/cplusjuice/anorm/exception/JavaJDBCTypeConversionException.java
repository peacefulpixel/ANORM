package com.cplusjuice.anorm.exception;

public class JavaJDBCTypeConversionException extends Exception {

    public static JavaJDBCTypeConversionException unknownType(int JDBCType) {
        return new JavaJDBCTypeConversionException("Unknown JDBC type: " + JDBCType);
    }

    public static JavaJDBCTypeConversionException unknownType(Class javaType) {
        return new JavaJDBCTypeConversionException("Unknown Java type: " + javaType.getName());
    }

    private JavaJDBCTypeConversionException(String message) {
        super(message);
    }
}
