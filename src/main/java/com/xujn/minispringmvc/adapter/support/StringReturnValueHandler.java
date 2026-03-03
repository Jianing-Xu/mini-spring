package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

/**
 * Writes String return values directly to the response body.
 * Constraint: Phase 1 has view resolution disabled, so String always means response body text.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class StringReturnValueHandler implements HandlerMethodReturnValueHandler, Ordered {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == String.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        if (!response.isCommitted()) {
            response.write(returnValue == null ? "" : returnValue.toString());
        }
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
