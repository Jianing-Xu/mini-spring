package com.xujn.minispringmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an HTTP method and path mapping for a controller class or handler method.
 * Constraint: Phase 1 supports exact path matching and a single HTTP method value.
 * Thread-safety: annotation metadata is immutable.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String path() default "";

    String method() default "GET";
}
