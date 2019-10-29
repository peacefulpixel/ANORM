package com.cplusjuice.anorm.exception;

public class InvalidBeanException extends RuntimeException {

    public static InvalidBeanException haveNotAnnotationPresents(String className) {
        return new InvalidBeanException("Class " + className + " doesn't marked with @Presents");
    }

    public static InvalidBeanException haveNotConstructor(String className) {
        return new InvalidBeanException(
                "Class " + className + " doesn't contain the public constructor without arguments");
    }

    public static InvalidBeanException constructorIsNotPublic(String className) {
        return new InvalidBeanException(
                "Constructor of " + className + " is not public");
    }

    public static InvalidBeanException unableToGetInstance(String className) {
        return new InvalidBeanException(
                "Unable to get instance of " + className);
    }

    public static InvalidBeanException unableToFindSetter(String setterName) {
        return new InvalidBeanException("Unable to find setter by name: " + setterName);
    }

    public static InvalidBeanException unableToInvoke(String methodName) {
        return new InvalidBeanException("Unable to invoke " + methodName + " method");
    }

    private InvalidBeanException(String message) {
        super(message);
    }
}
