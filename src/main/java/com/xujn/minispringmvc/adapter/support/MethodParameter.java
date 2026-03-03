package com.xujn.minispringmvc.adapter.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Lightweight method parameter descriptor for MVC argument and return value handling.
 * Constraint: Phase 1 exposes only data required for annotation lookup, type checks, and error reporting.
 * Thread-safety: immutable after construction.
 */
public class MethodParameter {

    private final Method method;
    private final Class<?> parameterType;
    private final Annotation[] annotations;
    private final String parameterName;
    private final int parameterIndex;

    public MethodParameter(Method method, Class<?> parameterType, Annotation[] annotations, String parameterName, int parameterIndex) {
        this.method = Objects.requireNonNull(method, "method must not be null");
        this.parameterType = Objects.requireNonNull(parameterType, "parameterType must not be null");
        this.annotations = annotations == null ? new Annotation[0] : annotations.clone();
        this.parameterName = parameterName == null ? "" : parameterName;
        this.parameterIndex = parameterIndex;
    }

    public static MethodParameter forReturnType(Method method) {
        return new MethodParameter(method, method.getReturnType(), method.getAnnotations(), method.getName() + "#return", -1);
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public String getParameterName() {
        return parameterName;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public boolean hasParameterAnnotation(Class<? extends Annotation> annotationType) {
        return getParameterAnnotation(annotationType) != null;
    }

    public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }
}
