package com.cplusjuice.anorm;

public interface Configuration {
    SqlDriver getDriver();
    void setDriver(SqlDriver value);

    String getLocation();
    void setLocation(String value);

    String getLogin();
    void setLogin(String value);

    String getPassword();
    void setPassword(String value);
}