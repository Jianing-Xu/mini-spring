package com.xujn.minispringmvc.test.phase3.shortcircuit;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.interceptor.HandlerInterceptor;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

@Component
public class ShortCircuitInterceptor implements HandlerInterceptor, Ordered {

    private final ShortCircuitTraceRecorder recorder;

    public ShortCircuitInterceptor(ShortCircuitTraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean preHandle(WebRequest request, WebResponse response, Object handler) {
        recorder.record("pre:short");
        response.setStatus(403);
        response.write("blocked");
        return false;
    }

    @Override
    public void postHandle(WebRequest request, WebResponse response, Object handler, ModelAndView modelAndView) {
        recorder.record("post:short");
    }

    @Override
    public void afterCompletion(WebRequest request, WebResponse response, Object handler, Exception ex) {
        recorder.record("after:short");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
