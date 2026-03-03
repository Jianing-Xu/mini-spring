package com.xujn.minispringmvc.test.phase1.multiadapter;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerAdapter;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

@Component
public class SecondConflictingAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof MultiAdapterHandler;
    }

    @Override
    public ModelAndView handle(WebRequest request, WebResponse response, Object handler) {
        response.write("second");
        return ModelAndView.empty();
    }
}
