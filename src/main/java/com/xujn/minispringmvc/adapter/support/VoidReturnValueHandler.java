package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Completes handling for void return types without writing a body.
 * Constraint: Phase 1 does not infer a default view for void methods.
 * Thread-safety: stateless and thread-safe.
 */
public class VoidReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == void.class || returnType.getParameterType() == Void.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        // Intentionally no-op: void means the handler completed without a response body in Phase 1.
    }
}
