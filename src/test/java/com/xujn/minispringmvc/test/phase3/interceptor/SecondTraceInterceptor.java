package com.xujn.minispringmvc.test.phase3.interceptor;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.interceptor.HandlerInterceptor;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;

@Component
public class SecondTraceInterceptor implements HandlerInterceptor, Ordered {

    private final InterceptorTraceRecorder recorder;

    public SecondTraceInterceptor(InterceptorTraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean preHandle(WebRequest request, WebResponse response, Object handler) {
        recorder.record("pre:second");
        return true;
    }

    @Override
    public void postHandle(WebRequest request, WebResponse response, Object handler, ModelAndView modelAndView) {
        recorder.record("post:second");
    }

    @Override
    public void afterCompletion(WebRequest request, WebResponse response, Object handler, Exception ex) {
        recorder.record("after:second");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
