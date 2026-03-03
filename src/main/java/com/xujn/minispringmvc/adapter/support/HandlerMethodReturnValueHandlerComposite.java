package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.exception.UnsupportedHandlerMethodReturnValueException;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered composite for handling controller return values.
 * Constraint: Phase 2 stops at the first handler that supports the return type.
 * Thread-safety: populated during init and then treated as immutable.
 */
public final class HandlerMethodReturnValueHandlerComposite {

    private final List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();

    public void addHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        this.handlers.clear();
        this.handlers.addAll(handlers);
    }

    public List<HandlerMethodReturnValueHandler> getHandlers() {
        return List.copyOf(handlers);
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        return handlers.stream().anyMatch(handler -> handler.supportsReturnType(returnType));
    }

    public ModelAndView handleReturnValue(
            Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) throws Exception {
        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler.supportsReturnType(returnType)) {
                return handler.handleReturnValue(returnValue, returnType, request, response);
            }
        }
        throw new UnsupportedHandlerMethodReturnValueException(
                returnType.getMethod().getDeclaringClass().getName() + "#" + returnType.getMethod().getName(),
                returnType.getParameterType().getName());
    }
}
