package com.xujn.minispringmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for direct response body rendering.
 * Constraint: Phase 1 keeps String return values as direct response bodies even without this annotation.
 * Thread-safety: annotation metadata is immutable.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
}
