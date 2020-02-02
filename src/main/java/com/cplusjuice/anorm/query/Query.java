package com.cplusjuice.anorm.query;

import com.cplusjuice.anorm.bean.Presents;
import com.cplusjuice.anorm.exception.InvalidBeanException;
import com.cplusjuice.anorm.exception.InvalidSqlTableException;
import com.cplusjuice.anorm.exception.JavaJDBCTypeConversionException;
import com.cplusjuice.anorm.exception.QueryExecutingException;
import com.cplusjuice.anorm.util.CaseFormatter;
import com.cplusjuice.anorm.util.JavaJDBCTypesConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.cplusjuice.anorm.ANORM.getConnection;
import static com.cplusjuice.anorm.query.Expression.alwaysTrue;
import static com.cplusjuice.anorm.util.CaseFormat.CAMEL_CASE;
import static com.cplusjuice.anorm.util.CaseFormat.SNAKE_CASE;

public class Query<T> {
    private Class<T> tClass;
    private String tableName;

    public Query(Class<T> tClass) {
        this.tClass = tClass;

        if (!tClass.isAnnotationPresent(Presents.class)) {
            throw InvalidBeanException.haveNotAnnotationPresents(tClass.getName());
        }

        Presents presents = tClass.getAnnotation(Presents.class);
        tableName = presents.value();
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

    private void set(T bean, String columnName, int sqlType, ResultSet set)
            throws JavaJDBCTypeConversionException {

        JavaJDBCTypesConverter converter = new JavaJDBCTypesConverter(sqlType);
        Class javaType = converter.asJavaType();
        Object value = converter.invokeGetValue(set, columnName);

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
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(query);
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
        } catch (SQLException | JavaJDBCTypeConversionException e) {
            throw InvalidSqlTableException.somethingHappened();
            // TODO: Catch JavaJDBCTypeConversionException
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

    public boolean isTableExists() {
        final int JDBC_TABLE_NAME_COLUMN_INDEX = 3;

        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", null);

            while (resultSet.next()) {
                String tableName = resultSet.getString(JDBC_TABLE_NAME_COLUMN_INDEX);

                if (tableName.equals(this.tableName)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: REMOVE
        }

        return false;
    }

    public boolean createTable() {
        if (isTableExists()) {
            return false;
        }

        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(tableName).append(" ( ");

        Method[] methods = tClass.getMethods();
        for (Method method : methods) {

            String name = method.getName();
            int nameStart = name.startsWith("get") ? 3 : (name.startsWith("is") ? 2 : -1);
            if (nameStart != -1 && !name.equals("getClass")) {
                String nameInCamel = name.substring(nameStart);

                CaseFormatter formatter = new CaseFormatter(nameInCamel);
                String nameInSnake = formatter.convert(SNAKE_CASE);

                Class returnType = method.getReturnType();
                JavaJDBCTypesConverter converter;
                try {
                    converter = new JavaJDBCTypesConverter(returnType);
                } catch (JavaJDBCTypeConversionException e) {
                    e.printStackTrace();
                    return false;
                }

                builder.append(nameInSnake)
                        .append(" ")
                        .append(converter.getJDBCName())
                        .append(", ");
            }
        }

        int length = builder.length();
        builder.delete(length - 2, length).append(" )");

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(builder.toString());
        } catch (SQLException e) {
            throw QueryExecutingException.errorWhileExecuting();
        }

        return true;
    }
}