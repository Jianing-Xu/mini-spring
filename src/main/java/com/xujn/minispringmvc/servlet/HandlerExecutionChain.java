package com.xujn.minispringmvc.servlet;

import com.xujn.minispringmvc.interceptor.HandlerInterceptor;

import java.util.List;
import java.util.Objects;

/**
 * Runtime wrapper for a selected handler and its interceptor chain.
 * Constraint: Phase 3 records the last successfully executed preHandle index for precise afterCompletion callbacks.
 * Thread-safety: request-scoped state is mutable and not designed for concurrent use.
 */
public class HandlerExecutionChain {

    private final Object handler;
    private final List<HandlerInterceptor> interceptors;
    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler) {
        this(handler, List.of());
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptors) {
        this.handler = Objects.requireNonNull(handler, "handler must not be null");
        this.interceptors = interceptors == null ? List.of() : List.copyOf(interceptors);
    }

    public Object getHandler() {
        return handler;
    }

    public List<HandlerInterceptor> getInterceptors() {
        return interceptors;
    }

    public boolean applyPreHandle(WebRequest request, WebResponse response) throws Exception {
        for (int index = 0; index < interceptors.size(); index++) {
            HandlerInterceptor interceptor = interceptors.get(index);
            if (!interceptor.preHandle(request, response, handler)) {
                interceptorIndex = index - 1;
                return false;
            }
            interceptorIndex = index;
        }
        return true;
    }

    public void applyPostHandle(WebRequest request, WebResponse response, ModelAndView modelAndView) throws Exception {
        for (int index = interceptorIndex; index >= 0; index--) {
            interceptors.get(index).postHandle(request, response, handler, modelAndView);
        }
    }

    public void triggerAfterCompletion(WebRequest request, WebResponse response, Exception ex) throws Exception {
        for (int index = interceptorIndex; index >= 0; index--) {
            interceptors.get(index).afterCompletion(request, response, handler, ex);
        }
    }
}
