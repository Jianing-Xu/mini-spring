package com.xujn.minispringmvc.test.phase3.exception;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.interceptor.HandlerInterceptor;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

@Component
public class ExceptionTraceInterceptor implements HandlerInterceptor, Ordered {

    private final ExceptionTraceRecorder recorder;

    public ExceptionTraceInterceptor(ExceptionTraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean preHandle(WebRequest request, WebResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(WebRequest request, WebResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(WebRequest request, WebResponse response, Object handler, Exception ex) {
        recorder.record("afterCompletion");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
