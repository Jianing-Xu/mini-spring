package com.xujn.minispringmvc.exception;

/**
 * Raised when no handler mapping matches the current request.
 * Constraint: message includes HTTP method and request path for acceptance visibility.
 * Thread-safety: immutable exception state after construction.
 */
public class NoHandlerFoundException extends MvcException {

    public NoHandlerFoundException(String method, String path) {
        super("No handler found for request [" + method + " " + path + "]");
    }
}
