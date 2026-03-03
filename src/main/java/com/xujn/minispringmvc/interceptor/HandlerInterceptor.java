package com.xujn.minispringmvc.interceptor;

import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Intercepts handler execution before and after the controller invocation.
 * Constraint: Phase 3 executes preHandle in order and postHandle/afterCompletion in reverse order.
 * Thread-safety: interceptors are initialized during bootstrap and then treated as read-only.
 */
public interface HandlerInterceptor {

    boolean preHandle(WebRequest request, WebResponse response, Object handler) throws Exception;

    void postHandle(WebRequest request, WebResponse response, Object handler, ModelAndView modelAndView) throws Exception;

    void afterCompletion(WebRequest request, WebResponse response, Object handler, Exception ex) throws Exception;
}
