package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

/**
 * Converts String return values into logical view names when view resolution is enabled.
 * Constraint: active only when the dispatcher sees at least one ViewResolver.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class ViewNameReturnValueHandler implements HandlerMethodReturnValueHandler, Ordered {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == String.class;
    }

    @Override
    public ModelAndView handleReturnValue(
            Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        return returnValue == null ? ModelAndView.empty() : new ModelAndView(returnValue.toString());
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
