package com.xujn.minispringmvc.exception;

/**
 * Raised when no handler adapter can execute the resolved handler.
 * Constraint: this is a bootstrap/configuration error and must fail fast instead of being rendered as a normal 500 response.
 * Thread-safety: immutable exception state after construction.
 */
public class NoHandlerAdapterException extends MvcException {

    public NoHandlerAdapterException(String handlerType) {
        super("No HandlerAdapter for handler type [" + handlerType + "]");
    }
}
