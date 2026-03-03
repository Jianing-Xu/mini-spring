package com.xujn.minispringmvc.test.phase2.override;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodArgumentResolver;
import com.xujn.minispringmvc.adapter.support.MethodParameter;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.PriorityOrdered;

@Component
public class CustomHighPriorityRequestParamResolver implements HandlerMethodArgumentResolver, PriorityOrdered {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParam.class) && parameter.getParameterType() == String.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        return "custom-" + request.getParameter(requestParam.value());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
