package com.xujn.minispringmvc.test.phase1.multiadapter;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.mapping.HandlerMapping;
import com.xujn.minispringmvc.servlet.HandlerExecutionChain;
import com.xujn.minispringmvc.servlet.WebRequest;

@Component
public class MultiAdapterHandlerMapping implements HandlerMapping {

    @Override
    public HandlerExecutionChain getHandler(WebRequest request) {
        if ("/multi-adapter".equals(request.getRequestUri())) {
            return new HandlerExecutionChain(new MultiAdapterHandler());
        }
        return null;
    }
}
