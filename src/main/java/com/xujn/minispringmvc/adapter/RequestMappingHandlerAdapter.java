package com.xujn.minispringmvc.adapter;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.support.InvocableHandlerMethod;
import com.xujn.minispringmvc.adapter.support.RequestParamArgumentResolver;
import com.xujn.minispringmvc.adapter.support.StringReturnValueHandler;
import com.xujn.minispringmvc.adapter.support.VoidReturnValueHandler;
import com.xujn.minispringmvc.adapter.support.WebRequestArgumentResolver;
import com.xujn.minispringmvc.adapter.support.WebResponseArgumentResolver;
import com.xujn.minispringmvc.mapping.HandlerMethod;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

import java.util.List;

/**
 * Default adapter that executes HandlerMethod instances.
 * Constraint: Phase 1 hardcodes simple argument resolvers and String/void return value handlers.
 * Thread-safety: initialized once and then read-only.
 */
@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter, Ordered {

    private final List<HandlerMethodArgumentResolver> argumentResolvers = List.of(
            new WebRequestArgumentResolver(),
            new WebResponseArgumentResolver(),
            new RequestParamArgumentResolver()
    );
    private final List<HandlerMethodReturnValueHandler> returnValueHandlers = List.of(
            new VoidReturnValueHandler(),
            new StringReturnValueHandler()
    );

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public void handle(WebRequest request, WebResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        new InvocableHandlerMethod(handlerMethod, argumentResolvers, returnValueHandlers).invokeForRequest(request, response);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
