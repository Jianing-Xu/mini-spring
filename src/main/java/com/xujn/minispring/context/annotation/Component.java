package com.xujn.minispring.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a candidate bean for component scanning.
 * Constraint: Phase 1 registers only concrete classes annotated with this marker.
 * Thread-safety: annotation type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    String value() default "";
}
