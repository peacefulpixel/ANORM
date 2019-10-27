package com.cplusjuice.anorm.query;

import com.cplusjuice.anorm.ANORM;
import com.cplusjuice.anorm.bean.Presents;
import com.cplusjuice.anorm.exception.InvalidBeanException;
import com.cplusjuice.anorm.exception.QueryExecutingException;
import com.cplusjuice.anorm.util.CaseFormat;
import com.cplusjuice.anorm.util.CaseFormatter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.cplusjuice.anorm.query.Expression.alwaysTrue;
import static com.cplusjuice.anorm.util.CaseFormat.CAMEL_CASE;
import static com.cplusjuice.anorm.util.CaseFormat.SNAKE_CASE;

public class Query<T> {
    Class<T> tClass;
    String tableName;
    private Statement statement;

    public Query(Class<T> tClass) {
        this.tClass = tClass;

        if (!tClass.isAnnotationPresent(Presents.class)) {
            throw InvalidBeanException.haveNotAnnotationPresents(tClass.getName());
        }

        Presents presents = tClass.getAnnotation(Presents.class);
        tableName = presents.value();

        try {
            statement = ANORM.getConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: remove printStackTrace call
        }
    }

    private Statement getStatement() {
        if (statement == null) {
            // TODO: Replace exception class
            throw new NullPointerException("Can't get Statement instance from SQL Connection");
        }

        return statement;
    }

    private T getInstance() {
        Constructor<T> constructor;
        try {
            constructor = tClass.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
            // TODO: remove printStackTrace call and return statement
        }

        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
            // TODO: remove printStackTrace call and return statement
        }
    }

    private Method getSetter(String fieldName, Class type) {
        CaseFormatter formatter = new CaseFormatter(fieldName);
        String fieldNameInCamel = formatter.convert(CAMEL_CASE);

        // What the fuck is "is" doing here?
        String methodName = (type.equals(Boolean.class) ? "is" : "set") +
                new String(new char[] { fieldNameInCamel.charAt(0) }).toUpperCase() +
                fieldNameInCamel.substring(1);

        try {
            return tClass.getMethod(methodName, type);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
            // TODO: remove printStackTrace call and return statement
        }
    }

    private void set(T bean, String columnName, int sqlType, ResultSet set) throws SQLException {
        Class javaType;
        Object value;

        if (sqlType == Types.BOOLEAN) {
            javaType = Boolean.class;
            value = set.getBoolean(columnName);
        } else if (sqlType == Types.DATE) {
            javaType = Date.class;
            value = set.getDate(columnName);
        } else if (sqlType == Types.DOUBLE) {
            javaType = Double.class;
            value = set.getDouble(columnName);
        } else if (sqlType == Types.FLOAT) {
            javaType = Float.class;
            value = set.getFloat(columnName);
        } else if (sqlType == Types.INTEGER) {
            javaType = Integer.class;
            value = set.getInt(columnName);
        } else if (sqlType == Types.TIMESTAMP) {
            javaType = Timestamp.class;
            value = set.getTimestamp(columnName);
        } else if (sqlType == Types.VARCHAR) {
            javaType = String.class;
            value = set.getString(columnName);
        } else {
            throw new RuntimeException("Unknown column type: " + sqlType);
            // TODO: Replace with other exception
        }

        Method setter = getSetter(columnName, javaType);
        try {
            if (setter != null) {
                setter.invoke(bean, value);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            // TODO: remove printStackTrace call
        }
    }

    public List<T> select() {
        return select(alwaysTrue());
    }

    public List<T> select(Expression expression) {
        List<T> result = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " WHERE " + expression.toString();

        ResultSet resultSet;
        try {
            getStatement().execute(query);
            resultSet = statement.getResultSet();

            while (resultSet.next()) {
                T i = getInstance();

                ResultSetMetaData data = resultSet.getMetaData();
                for (int index = 1; index <= data.getColumnCount(); index++) {
                    set(i, data.getColumnName(index),
                            data.getColumnType(index), resultSet);
                }

                result.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
            // TODO: remove printStackTrace call and return statement
        }

        return result;
    }

    public T selectOne() {
        return selectOne(alwaysTrue());
    }

    public T selectOne(Expression expression) {
        List<T> all = select(expression);
        if (all.size() > 1) {
            throw QueryExecutingException.moreThanOneRow();
        }

        if (all.isEmpty()) {
            throw QueryExecutingException.zeroRows();
        }

        return all.get(0);
    }
}