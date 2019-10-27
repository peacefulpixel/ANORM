package com.cplusjuice.anorm;

import org.jetbrains.annotations.Nullable;

public abstract class Configuration {
    public abstract SqlDriver getDriver();
    public abstract String getLocation();

    @Nullable
    public String getLogin() {
        return null;
    }

    @Nullable
    public String getPassword() {
        return null;
    }
}
