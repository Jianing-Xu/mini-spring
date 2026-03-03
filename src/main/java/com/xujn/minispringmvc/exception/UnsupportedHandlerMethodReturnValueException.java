package com.xujn.minispringmvc.exception;

/**
 * Raised when no return value handler supports a controller method return type.
 * Constraint: this is treated as a configuration error and must fail fast.
 * Thread-safety: immutable exception state after construction.
 */
public class UnsupportedHandlerMethodReturnValueException extends MvcException {

    public UnsupportedHandlerMethodReturnValueException(String handlerSignature, String returnType) {
        super("No return value handler for handler [" + handlerSignature + "], return type [" + returnType + "]");
    }
}
