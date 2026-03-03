package com.xujn.minispring.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a JavaConfig configuration source.
 * Constraint: configuration classes are discovered through component scanning and are not CGLIB-enhanced.
 * Thread-safety: annotation metadata is immutable.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Configuration {
}
