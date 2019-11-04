package com.cplusjuice.anorm.exception;

public class QueryExecutingException extends RuntimeException {

    public static QueryExecutingException moreThanOneRow() {
        return new QueryExecutingException(
                "Connection was not initialized. For first create a ANORM instance.");
    }

    public static QueryExecutingException zeroRows() {
        return new QueryExecutingException("Query returns zero rows");
    }

    public static QueryExecutingException errorWhileExecuting() {
        return new QueryExecutingException("An error has occurred while executing a query");
    }

    private QueryExecutingException(String message) {
        super(message);
    }
}
