package com.cplusjuice.anorm.exception;

public class NotInitializedConnectionException extends RuntimeException {

    public static NotInitializedConnectionException notInitialized() {
        return new NotInitializedConnectionException(
                "Connection was not initialized. For first create a ANORM instance.");
    }

    private NotInitializedConnectionException(String message) {
        super(message);
    }
}
