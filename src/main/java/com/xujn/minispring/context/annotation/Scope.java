package com.xujn.minispring.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares bean scope metadata.
 * Constraint: Phase 1 accepts singleton only and rejects other values during scanning.
 * Thread-safety: annotation type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {

    String value();
}
