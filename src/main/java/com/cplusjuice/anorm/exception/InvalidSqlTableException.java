package com.cplusjuice.anorm.exception;

public class InvalidSqlTableException extends RuntimeException {

    public static InvalidSqlTableException invalidColumnType(int type) {
        return new InvalidSqlTableException("Unknown column type: " + type);
    }

    public static InvalidSqlTableException somethingHappened() {
        return new InvalidSqlTableException("An error was occurred while processing the query");
    }

    private InvalidSqlTableException(String message) {
        super(message);
    }
}
