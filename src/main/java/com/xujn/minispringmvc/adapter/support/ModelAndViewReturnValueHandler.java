package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

/**
 * Returns ModelAndView values directly to the dispatcher for rendering.
 * Constraint: active only when view resolution is enabled by the dispatcher.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class ModelAndViewReturnValueHandler implements HandlerMethodReturnValueHandler, Ordered {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return ModelAndView.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public ModelAndView handleReturnValue(
            Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        return returnValue == null ? ModelAndView.empty() : (ModelAndView) returnValue;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
