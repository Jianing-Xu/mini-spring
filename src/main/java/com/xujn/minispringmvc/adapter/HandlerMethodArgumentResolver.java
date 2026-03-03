package com.xujn.minispringmvc.adapter;

import com.xujn.minispringmvc.adapter.support.MethodParameter;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Strategy interface for resolving one handler method argument from the current request.
 * Constraint: Phase 1 uses an internal ordered chain and stops at the first supporting resolver.
 * Thread-safety: resolvers are configured during bootstrap and then treated as read-only.
 */
public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) throws Exception;
}
