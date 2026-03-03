package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

/**
 * Completes handling for void return types without writing a body.
 * Constraint: Phase 1 does not infer a default view for void methods.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class VoidReturnValueHandler implements HandlerMethodReturnValueHandler, Ordered {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == void.class || returnType.getParameterType() == Void.class;
    }

    @Override
    public ModelAndView handleReturnValue(
            Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        // Intentionally no-op: void means the handler completed without a response body in Phase 1.
        return ModelAndView.empty();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
