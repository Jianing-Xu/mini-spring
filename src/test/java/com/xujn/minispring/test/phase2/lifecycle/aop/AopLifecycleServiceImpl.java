package com.xujn.minispring.test.phase2.lifecycle.aop;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.context.annotation.Component;

@Component
public class AopLifecycleServiceImpl implements AopLifecycleService, InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() {
        AopLifecycleState.initialized = true;
    }

    @Override
    public void destroy() {
        AopLifecycleState.destroyed = true;
    }

    @Override
    public String work() {
        return "lifecycle";
    }
}
