package com.xujn.minispring.tx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a local transaction boundary on a type or method.
 * Constraint: MVP semantics are fixed to REQUIRED propagation with default rollback rules.
 * Thread-safety: annotation metadata is immutable.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}
