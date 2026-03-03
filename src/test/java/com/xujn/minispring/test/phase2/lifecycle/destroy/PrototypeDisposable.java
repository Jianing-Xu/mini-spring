package com.xujn.minispring.test.phase2.lifecycle.destroy;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.context.annotation.Scope;

@Component
@Scope("prototype")
public class PrototypeDisposable implements DisposableBean {

    @Override
    public void destroy() {
        DisposableState.prototypeDestroyed = true;
    }
}
