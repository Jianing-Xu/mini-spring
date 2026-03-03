package com.xujn.minispringmvc.servlet;

import java.util.Objects;

/**
 * Runtime wrapper for a selected handler and its request-scoped execution metadata.
 * Constraint: Phase 1 carries only the handler and does not include interceptors.
 * Thread-safety: immutable after construction.
 */
public class HandlerExecutionChain {

    private final Object handler;

    public HandlerExecutionChain(Object handler) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
    }

    public Object getHandler() {
        return handler;
    }
}
