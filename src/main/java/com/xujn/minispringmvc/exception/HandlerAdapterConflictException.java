package com.xujn.minispringmvc.exception;

/**
 * Raised when multiple handler adapters claim they can execute the same handler.
 * Constraint: adapter selection must be deterministic, so multiple matches are treated as a configuration error.
 * Thread-safety: immutable exception state after construction.
 */
public class HandlerAdapterConflictException extends MvcException {

    public HandlerAdapterConflictException(String handlerType, int count, String candidates) {
        super("Multiple HandlerAdapters for handler type [" + handlerType + "], count [" + count +
                "], candidates [" + candidates + "]");
    }
}
