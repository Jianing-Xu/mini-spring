package com.xujn.minispring.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares package roots for component scanning.
 * Constraint: Phase 1 only uses value-based package scanning.
 * Thread-safety: annotation type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {

    String[] value() default {};
}
