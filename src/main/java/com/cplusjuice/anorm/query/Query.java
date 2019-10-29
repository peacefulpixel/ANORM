package com.cplusjuice.anorm.query;

import com.cplusjuice.anorm.ANORM;
import com.cplusjuice.anorm.bean.Presents;
import com.cplusjuice.anorm.exception.InvalidBeanException;
import com.cplusjuice.anorm.exception.InvalidSqlTableException;
import com.cplusjuice.anorm.exception.JDBCStatementException;
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
            throw JDBCStatementException.cantCreate();
        }
    }

    private Statement getStatement() {
        if (statement == null) {
            throw JDBCStatementException.uninitialized();
        }

        return statement;
    }

    private T getInstance() {
        Constructor<T> constructor;
        try {
            constructor = tClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw InvalidBeanException.haveNotConstructor(tClass.getName());
        }

        try {
            return constructor.newInstance();
        } catch (InstantiationException | InvocationTargetException e) {
            throw InvalidBeanException.unableToGetInstance(tClass.getName());
        } catch (IllegalAccessException e) {
            throw InvalidBeanException.constructorIsNotPublic(tClass.getName());
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
            throw InvalidBeanException.unableToFindSetter(methodName);
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
            throw InvalidSqlTableException.invalidColumnType(sqlType);
        }

        Method setter = getSetter(columnName, javaType);
        try {
            if (setter != null) {
                setter.invoke(bean, value);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw InvalidBeanException.unableToInvoke(setter.getName());
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
            throw InvalidSqlTableException.somethingHappened();
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