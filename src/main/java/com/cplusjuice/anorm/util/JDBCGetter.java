package com.cplusjuice.anorm.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JDBCGetter {
    Object getValue(ResultSet instance, String name) throws SQLException;
}
