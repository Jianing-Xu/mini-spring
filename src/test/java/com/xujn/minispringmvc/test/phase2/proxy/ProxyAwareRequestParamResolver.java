package com.xujn.minispringmvc.test.phase2.proxy;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.support.MethodParameter;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

@Component
public class ProxyAwareRequestParamResolver implements ProxyResolverDelegate {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParam.class) && parameter.getParameterType() == String.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        return "proxy-" + request.getParameter(requestParam.value());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
