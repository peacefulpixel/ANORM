package com.cplusjuice.anorm.exception;

public class FailedToConnectException extends RuntimeException {

    public static FailedToConnectException cantConnect(String address) {
        return new FailedToConnectException("Can't connect to " + address);
    }

    public static FailedToConnectException classNotFound(String className) {
        return new FailedToConnectException("Class " + className + " was not found");
    }

    private FailedToConnectException(String message) {
        super(message);
    }
}
