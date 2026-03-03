package com.xujn.minispringmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a request parameter to a handler method argument.
 * Constraint: Phase 1 requires explicit parameter names for simple type binding.
 * Thread-safety: annotation metadata is immutable.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    String value();

    boolean required() default true;
}
