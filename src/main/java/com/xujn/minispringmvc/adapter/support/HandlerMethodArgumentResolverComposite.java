package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispringmvc.adapter.HandlerMethodArgumentResolver;
import com.xujn.minispringmvc.exception.UnsupportedHandlerMethodParameterException;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered composite for resolving handler method arguments.
 * Constraint: Phase 2 stops at the first resolver that supports a parameter.
 * Thread-safety: populated during init and then treated as immutable.
 */
public final class HandlerMethodArgumentResolverComposite {

    private final List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    public void addResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        this.resolvers.clear();
        this.resolvers.addAll(resolvers);
    }

    public List<HandlerMethodArgumentResolver> getResolvers() {
        return List.copyOf(resolvers);
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return resolvers.stream().anyMatch(resolver -> resolver.supportsParameter(parameter));
    }

    public Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) throws Exception {
        for (HandlerMethodArgumentResolver resolver : resolvers) {
            if (resolver.supportsParameter(parameter)) {
                return resolver.resolveArgument(parameter, request, response);
            }
        }
        throw new UnsupportedHandlerMethodParameterException(
                parameter.getMethod().getDeclaringClass().getName() + "#" + parameter.getMethod().getName(),
                parameter.getParameterIndex(),
                parameter.getParameterType().getName());
    }
}
