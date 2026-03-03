package com.xujn.minispringmvc.exception;

/**
 * Raised when no argument resolver supports a handler method parameter.
 * Constraint: this is treated as a configuration error and must fail fast.
 * Thread-safety: immutable exception state after construction.
 */
public class UnsupportedHandlerMethodParameterException extends MvcException {

    public UnsupportedHandlerMethodParameterException(String handlerSignature, int parameterIndex, String parameterType) {
        super("No argument resolver for handler [" + handlerSignature + "], parameter index [" +
                parameterIndex + "], type [" + parameterType + "]");
    }
}
