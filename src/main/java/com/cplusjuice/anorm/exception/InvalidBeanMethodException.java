package com.cplusjuice.anorm.exception;

public class InvalidBeanMethodException extends RuntimeException {

    public static InvalidBeanMethodException invalidName(String name) {
        return new InvalidBeanMethodException("Invalid bean getter name: " + name);
    }

    private InvalidBeanMethodException(String message) {
        super(message);
    }
}
