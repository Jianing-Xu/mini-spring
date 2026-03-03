package com.xujn.minispring.context.annotation;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Metadata describing a single {@link Bean} factory method.
 * Constraint: stores only the minimal Phase 1 factory-method metadata.
 * Thread-safety: immutable after construction.
 */
public class BeanMethod {

    private final Method method;
    private final String beanName;
    private final Class<?> returnType;

    public BeanMethod(Method method, String beanName, Class<?> returnType) {
        this.method = Objects.requireNonNull(method, "method must not be null");
        this.beanName = Objects.requireNonNull(beanName, "beanName must not be null");
        this.returnType = Objects.requireNonNull(returnType, "returnType must not be null");
    }

    public Method getMethod() {
        return method;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }
}
