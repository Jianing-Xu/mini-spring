package com.xujn.minispring.test.phase2.lifecycle.destroy;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.context.annotation.Component;

@Component
public class FailingDisposable implements DisposableBean {

    @Override
    public void destroy() {
        throw new RuntimeException("boom");
    }
}
