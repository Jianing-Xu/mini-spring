package com.xujn.minispring.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a factory method on a {@link Configuration} class.
 * Constraint: JavaConfig Phase 1 supports only no-arg factory methods and singleton beans.
 * Thread-safety: annotation metadata is immutable.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    String[] name() default {};

    String[] value() default {};

    String initMethod() default "";

    String destroyMethod() default "";
}
