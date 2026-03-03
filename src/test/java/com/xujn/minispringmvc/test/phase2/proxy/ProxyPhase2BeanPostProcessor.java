package com.xujn.minispringmvc.test.phase2.proxy;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.context.annotation.Component;

import java.lang.reflect.Proxy;

@Component
public class ProxyPhase2BeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof ProxyResolverDelegate resolver) {
            return Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    new Class<?>[]{ProxyResolverDelegate.class},
                    (proxy, method, args) -> method.invoke(resolver, args)
            );
        }
        if (bean instanceof ProxyReturnHandlerDelegate handler) {
            return Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    new Class<?>[]{ProxyReturnHandlerDelegate.class},
                    (proxy, method, args) -> method.invoke(handler, args)
            );
        }
        return bean;
    }
}
