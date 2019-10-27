package com.cplusjuice.anorm.query;

import com.cplusjuice.anorm.exception.InvalidBeanMethodException;
import com.cplusjuice.anorm.util.CaseFormatter;

import java.lang.reflect.Method;

import static com.cplusjuice.anorm.query.ExpressionOperator.*;
import static com.cplusjuice.anorm.query.ExpressionOperatorType.MONO;
import static com.cplusjuice.anorm.util.CaseFormat.SNAKE_CASE;

public class Expression {

    private String fieldLeft;
    private String fieldRight;
    private ExpressionOperator operator;

    public static Expression alwaysTrue() {
        return new Expression("1", "1", EQUALS);
    }

    public static Expression eq(Object left, Object right) {
        return new Expression(left, right, EQUALS);
    }

    public static Expression notEq(Object left, Object right) {
        return new Expression(left, right, NOT_EQUALS);
    }

    public static Expression isNull(Object field) {
        return new Expression(field, IS_NULL);
    }

    public static Expression notNull(Object field) {
        return new Expression(field, NOT_NULL);
    }

    public static Expression not(Object field) {
        return new Expression(field, NOT);
    }

    public static Expression or(Object left, Object right) {
        return new Expression(left, right, OR);
    }

    public static Expression and(Object left, Object right) {
        return new Expression(left, right, AND);
    }

    public static Expression gt(Object left, Object right) {
        return new Expression(left, right, GREATER_THEN);
    }

    public static Expression lt(Object left, Object right) {
        return new Expression(left, right, LESSER_THEN);
    }

    public Expression(Object field,
                      ExpressionOperator operator) {

        fieldLeft = asString(field);
        this.operator = operator;
    }

    public Expression(Object fieldLeft,
                      Object fieldRight,
                      ExpressionOperator operator) {

        this.fieldLeft = asString(fieldLeft);
        this.fieldRight = asString(fieldRight);
        this.operator = operator;
    }

    @Override
    public String toString() {
        if (operator.getType().equals(MONO)) {
            if (operator.isMonoToLeft()) {
                return "(" + fieldLeft + " " + operator.getValue() + ")";
            } else {
                return "(" + operator.getValue() + " " + fieldLeft + ")";
            }
        } else {
            return "(" + fieldLeft + " " + operator.getValue() + " " + fieldRight + ")";
        }
    }

    private String asString(Object object) {
        if (object == null) {
            return "null";
        }

        if (object instanceof String) {
            return "'" + (String) object + "'";
        }

        if (object instanceof Method) {
            Method method = (Method) object;
            String name = method.getName();

            if (name.startsWith("get")) {
                name = name.substring(3);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
            } else {
                throw InvalidBeanMethodException.invalidName(name);
            }

            CaseFormatter formatter = new CaseFormatter(name);
            String snakeName = formatter.convert(SNAKE_CASE);

            return snakeName;
        }

        return object.toString();
    }
}
