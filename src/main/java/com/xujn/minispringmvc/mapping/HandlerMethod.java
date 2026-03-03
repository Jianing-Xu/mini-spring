package com.xujn.minispringmvc.mapping;

import com.xujn.minispringmvc.adapter.support.MethodParameter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * Runtime descriptor for a controller bean and its target method.
 * Constraint: metadata is resolved from the original bean class while invocation may target a proxied bean instance.
 * Thread-safety: immutable after construction.
 */
public class HandlerMethod {

    private final String beanName;
    private final Object bean;
    private final Class<?> beanType;
    private final Method method;
    private final MethodParameter[] parameters;

    public HandlerMethod(String beanName, Object bean, Class<?> beanType, Method method) {
        this.beanName = Objects.requireNonNull(beanName, "beanName must not be null");
        this.bean = Objects.requireNonNull(bean, "bean must not be null");
        this.beanType = Objects.requireNonNull(beanType, "beanType must not be null");
        this.method = Objects.requireNonNull(method, "method must not be null");
        this.parameters = Arrays.stream(method.getParameters())
                .map(parameter -> new MethodParameter(method, parameter.getType(), parameter.getAnnotations(), parameter.getName(),
                        findParameterIndex(method, parameter)))
                .toArray(MethodParameter[]::new);
    }

    private static int findParameterIndex(Method method, java.lang.reflect.Parameter parameter) {
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index] == parameter) {
                return index;
            }
        }
        return -1;
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getBean() {
        return bean;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Method getMethod() {
        return method;
    }

    public Method getInvocableMethod() {
        if (method.getDeclaringClass().isInstance(bean)) {
            return method;
        }
        // Controllers may be proxied via JDK interfaces, so invocation must resolve the matching interface method.
        try {
            return bean.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(
                    "Cannot resolve invocable method for handler [" + getShortLogMessage() + "] on bean class [" +
                            bean.getClass().getName() + "]", ex);
        }
    }

    public MethodParameter[] getParameters() {
        return parameters.clone();
    }

    public MethodParameter getReturnType() {
        return MethodParameter.forReturnType(method);
    }

    public String getShortLogMessage() {
        return beanType.getName() + "#" + method.getName();
    }
}
