package com.xujn.minispringmvc.test.phase2.proxy;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.support.MethodParameter;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

@Component
public class ProxyAwareStringReturnValueHandler implements ProxyReturnHandlerDelegate {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == String.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        if (!response.isCommitted()) {
            response.write("proxy-handled-" + returnValue);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
