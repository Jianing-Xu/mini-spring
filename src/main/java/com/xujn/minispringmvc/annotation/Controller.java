package com.xujn.minispringmvc.annotation;

import com.xujn.minispring.context.annotation.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a bean class as an MVC controller managed by mini-spring.
 * Constraint: controllers are discovered from bean definitions, not from servlet container scanning.
 * Thread-safety: annotation metadata is immutable.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Controller {

    String value() default "";
}
