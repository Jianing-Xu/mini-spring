package com.xujn.minispring.examples.phase2.fixture;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class AppServiceImpl implements AppService, InitializingBean, DisposableBean {

    @Autowired
    private SingletonDep singletonDep;

    @Override
    public void afterPropertiesSet() {
        ExampleState.initialized = singletonDep != null;
    }

    @Override
    public void destroy() {
        ExampleState.destroyed = true;
    }

    @Override
    public String serve() {
        return "phase2";
    }
}
