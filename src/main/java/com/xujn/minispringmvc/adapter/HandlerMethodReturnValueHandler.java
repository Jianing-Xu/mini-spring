package com.xujn.minispringmvc.adapter;

import com.xujn.minispringmvc.adapter.support.MethodParameter;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Strategy interface for writing a controller return value to the response.
 * Constraint: Phase 1 supports String and void return types only.
 * Thread-safety: handlers are configured during bootstrap and then treated as read-only.
 */
public interface HandlerMethodReturnValueHandler {

    boolean supportsReturnType(MethodParameter returnType);

    void handleReturnValue(Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) throws Exception;
}
