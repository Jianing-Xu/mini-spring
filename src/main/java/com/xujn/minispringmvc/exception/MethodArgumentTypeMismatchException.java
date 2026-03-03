package com.xujn.minispringmvc.exception;

import java.lang.reflect.Method;

/**
 * Raised when a request parameter cannot be converted to the target argument type.
 * Constraint: message includes parameter name, raw value, target type, and handler method.
 * Thread-safety: immutable exception state after construction.
 */
public class MethodArgumentTypeMismatchException extends MvcException {

    public MethodArgumentTypeMismatchException(String parameterName, Class<?> targetType, String rawValue, Method method) {
        this(parameterName, targetType, rawValue, method, null);
    }

    public MethodArgumentTypeMismatchException(
            String parameterName, Class<?> targetType, String rawValue, Method method, Throwable cause) {
        super("Failed to convert request parameter [" + parameterName + "] with value [" + rawValue +
                "] to type [" + targetType.getSimpleName() + "] for handler [" +
                method.getDeclaringClass().getName() + "#" + method.getName() + "]", cause);
    }
}
