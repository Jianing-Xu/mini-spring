package com.xujn.minispringmvc.test.phase3.interceptor;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.interceptor.HandlerInterceptor;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

@Component
public class FirstTraceInterceptor implements HandlerInterceptor, Ordered {

    private final InterceptorTraceRecorder recorder;

    public FirstTraceInterceptor(InterceptorTraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean preHandle(WebRequest request, WebResponse response, Object handler) {
        recorder.record("pre:first");
        return true;
    }

    @Override
    public void postHandle(WebRequest request, WebResponse response, Object handler, ModelAndView modelAndView) {
        recorder.record("post:first");
    }

    @Override
    public void afterCompletion(WebRequest request, WebResponse response, Object handler, Exception ex) {
        recorder.record("after:first");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
