package com.cplusjuice.anorm.exception;

public class InvalidBeanException extends RuntimeException {

    public static InvalidBeanException haveNotAnnotationPresents(String className) {
        return new InvalidBeanException("Class " + className + " doesn't marked with @Presents");
    }

    private InvalidBeanException(String message) {
        super(message);
    }
}
