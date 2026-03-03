package com.xujn.minispringmvc.test.phase1.noadapter;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.mapping.HandlerMapping;
import com.xujn.minispringmvc.servlet.HandlerExecutionChain;
import com.xujn.minispringmvc.servlet.WebRequest;

@Component
public class NoAdapterHandlerMapping implements HandlerMapping {

    @Override
    public HandlerExecutionChain getHandler(WebRequest request) {
        if ("/no-adapter".equals(request.getRequestUri())) {
            return new HandlerExecutionChain(new NoAdapterHandler());
        }
        return null;
    }
}
