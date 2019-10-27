package com.cplusjuice.anorm.query;

import static com.cplusjuice.anorm.query.ExpressionOperatorType.MONO;
import static com.cplusjuice.anorm.query.ExpressionOperatorType.STEREO;

public enum ExpressionOperator {

    EQUALS("="),
    NOT_EQUALS("<>"),
    IS_NULL("is null", true),
    NOT_NULL("not null", true),
    NOT("not", false),
    OR("or"),
    AND("and"),
    GREATER_THEN(">"),
    LESSER_THEN("<");

    private String value;
    private ExpressionOperatorType type;
    private boolean monoToLeft;

    ExpressionOperator(String value) {
        this.value = value;
        this.type  = STEREO;
    }

    ExpressionOperator(String value, boolean monoToLeft) {
        this.value = value;
        this.type = MONO;
        this.monoToLeft = monoToLeft;
    }

    ExpressionOperatorType getType() {
        return type;
    }

    String getValue() {
        return value;
    }

    boolean isMonoToLeft() {
        return monoToLeft;
    }
}
