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
    private final Class<?>[] parameterTypes;
    private final String initMethodName;
    private final String destroyMethodName;

    public BeanMethod(Method method, String beanName, Class<?> returnType,
                      Class<?>[] parameterTypes, String initMethodName, String destroyMethodName) {
        this.method = Objects.requireNonNull(method, "method must not be null");
        this.beanName = Objects.requireNonNull(beanName, "beanName must not be null");
        this.returnType = Objects.requireNonNull(returnType, "returnType must not be null");
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes.clone();
        this.initMethodName = initMethodName == null ? "" : initMethodName;
        this.destroyMethodName = destroyMethodName == null ? "" : destroyMethodName;
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

    public Class<?>[] getParameterTypes() {
        return parameterTypes.clone();
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }
}
