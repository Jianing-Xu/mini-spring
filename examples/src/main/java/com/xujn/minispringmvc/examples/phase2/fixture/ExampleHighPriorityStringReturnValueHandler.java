package com.xujn.minispringmvc.examples.phase2.fixture;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.adapter.support.MethodParameter;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.PriorityOrdered;

@Component
public class ExampleHighPriorityStringReturnValueHandler implements HandlerMethodReturnValueHandler, PriorityOrdered {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == String.class
                && returnType.getMethod().getName().equals("override");
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, WebRequest request, WebResponse response) {
        if (!response.isCommitted()) {
            response.write("example-handled-" + returnValue);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
