package com.cplusjuice.anorm.exception;

public class JDBCStatementException extends RuntimeException {

    public static JDBCStatementException cantCreate() {
        return new JDBCStatementException("Unable to create JDBC Statement instance");
    }

    private JDBCStatementException(String message) {
        super(message);
    }
}
