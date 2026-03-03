package com.xujn.minispringmvc.exception;

import java.lang.reflect.Method;

/**
 * Raised when a required request parameter is absent.
 * Constraint: message includes the parameter name and target handler method.
 * Thread-safety: immutable exception state after construction.
 */
public class MissingRequestParameterException extends MvcException {

    public MissingRequestParameterException(String parameterName, Method method) {
        super("Missing required request parameter [" + parameterName + "] for handler [" +
                method.getDeclaringClass().getName() + "#" + method.getName() + "]");
    }
}
