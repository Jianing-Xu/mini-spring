package com.xujn.minispringmvc.test.phase3.exception;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.exception.ExceptionResolver;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.PriorityOrdered;

@Component
public class CustomPhase3ExceptionResolver implements ExceptionResolver, PriorityOrdered {

    private final ExceptionTraceRecorder recorder;

    public CustomPhase3ExceptionResolver(ExceptionTraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean supports(Exception ex, Object handler) {
        return ex instanceof IllegalArgumentException;
    }

    @Override
    public ModelAndView resolveException(WebRequest request, WebResponse response, Object handler, Exception ex) {
        recorder.record("customResolver");
        response.setStatus(418);
        response.write("custom-error");
        return ModelAndView.empty();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
