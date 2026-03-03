package com.xujn.minispringmvc.exception;

import com.xujn.minispring.exception.BeansException;

/**
 * Base runtime exception for MVC configuration and dispatch failures.
 * Constraint: messages must include handler, mapping, or parameter context when available.
 * Thread-safety: immutable exception state after construction.
 */
public class MvcException extends BeansException {

    public MvcException(String message) {
        super(message);
    }

    public MvcException(String message, Throwable cause) {
        super(message, cause);
    }
}
