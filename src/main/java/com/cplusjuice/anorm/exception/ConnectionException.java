package com.cplusjuice.anorm.exception;

public class ConnectionException extends RuntimeException {

    public static ConnectionException notInitialized() {
        return new ConnectionException(
                "ConnectionFactory was not initialized. For first create a ANORM instance.");
    }

    public static ConnectionException probablyLost() {
        return new ConnectionException(
                "Can't create sql Connection instance. Probably database connection was lost");
    }

    private ConnectionException(String message) {
        super(message);
    }
}
