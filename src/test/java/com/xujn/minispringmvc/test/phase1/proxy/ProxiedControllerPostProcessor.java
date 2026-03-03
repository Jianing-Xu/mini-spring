package com.xujn.minispringmvc.test.phase1.proxy;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

import java.lang.reflect.Proxy;

@Component
public class ProxiedControllerPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (!(bean instanceof ProxiedControllerContract controller)) {
            return bean;
        }
        return Proxy.newProxyInstance(
                bean.getClass().getClassLoader(),
                new Class<?>[]{ProxiedControllerContract.class},
                (proxy, method, args) -> method.invoke(controller, args)
        );
    }
}
