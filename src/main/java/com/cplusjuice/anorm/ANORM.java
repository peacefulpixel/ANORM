package com.cplusjuice.anorm;

import com.cplusjuice.anorm.exception.FailedToConnectException;
import com.cplusjuice.anorm.exception.NotInitializedConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ANORM {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            throw NotInitializedConnectionException.notInitialized();
        }

        return connection;
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

        try {
            if (login == null) {
                connection = DriverManager.getConnection(dbAddress);
            } else {
                connection = DriverManager.getConnection(dbAddress, login, password);
            }
        } catch (SQLException e) {
            throw FailedToConnectException.cantConnect(dbAddress);
        }
    }
}
