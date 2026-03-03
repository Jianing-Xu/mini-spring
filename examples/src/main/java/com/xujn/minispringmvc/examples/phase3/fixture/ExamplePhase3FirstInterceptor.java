package com.xujn.minispringmvc.examples.phase3.fixture;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.interceptor.HandlerInterceptor;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

@Component
public class ExamplePhase3FirstInterceptor implements HandlerInterceptor, Ordered {

    @Override
    public boolean preHandle(WebRequest request, WebResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(WebRequest request, WebResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(WebRequest request, WebResponse response, Object handler, Exception ex) {
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
