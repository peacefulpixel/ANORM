package com.cplusjuice.anorm.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Presents {
    /**
     * Returns the entity that the class presents
     * @return the entity that the class presents
     */
    String value();
}
