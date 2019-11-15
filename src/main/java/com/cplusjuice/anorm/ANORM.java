package com.cplusjuice.anorm;

import com.cplusjuice.anorm.exception.FailedToConnectException;
import com.cplusjuice.anorm.exception.ConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ANORM {

    private static ConnectionFactory connectionFactory;

    public static Connection getConnection() throws ConnectionException {
        if (connectionFactory == null) {
            throw ConnectionException.notInitialized();
        }

        try {
            return connectionFactory.getConnection();
        } catch (SQLException e) {
            throw ConnectionException.probablyLost();
        }
    }

    public ANORM(Configuration configuration) throws FailedToConnectException {
        final String driverClass = configuration.getDriver().getDriverClass();
        final String dbAddress = configuration.getDriver().getDriverPrefix() +
                configuration.getLocation();

        final String login    = configuration.getLogin();
        final String password = configuration.getPassword();

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw FailedToConnectException.classNotFound(driverClass);
        }

        if (login == null) {
            connectionFactory = () -> DriverManager.getConnection(dbAddress);
        } else {
            connectionFactory = () -> DriverManager.getConnection(dbAddress, login, password);
        }
    }
}
