package com.xujn.minispringmvc.adapter;

import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Adapts a resolved handler object to an executable invocation path.
 * Constraint: Phase 1 supports only HandlerMethod instances.
 * Thread-safety: adapters are initialized during MVC bootstrap and then read-only.
 */
public interface HandlerAdapter {

    boolean supports(Object handler);

    void handle(WebRequest request, WebResponse response, Object handler) throws Exception;
}
