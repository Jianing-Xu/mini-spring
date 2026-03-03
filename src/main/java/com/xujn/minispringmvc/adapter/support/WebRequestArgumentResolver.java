package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodArgumentResolver;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

/**
 * Injects the current WebRequest into controller methods.
 * Constraint: supports only parameters assignable from WebRequest.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class WebRequestArgumentResolver implements HandlerMethodArgumentResolver, Ordered {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return WebRequest.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) {
        return request;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
