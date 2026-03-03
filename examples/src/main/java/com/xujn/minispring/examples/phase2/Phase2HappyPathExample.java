package com.xujn.minispring.examples.phase2;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.examples.phase2.fixture.AppService;
import com.xujn.minispring.examples.phase2.fixture.ExampleState;
import com.xujn.minispring.examples.phase2.fixture.PrototypeBean;

import java.lang.reflect.Proxy;

/**
 * Manual verification entry for the Phase 2 lifecycle, BPP, AOP, and prototype happy path.
 */
public class Phase2HappyPathExample {

    public static void main(String[] args) {
        ExampleState.reset();
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase2.fixture");

        AppService appService = context.getBean(AppService.class);
        PrototypeBean first = context.getBean(PrototypeBean.class);
        PrototypeBean second = context.getBean(PrototypeBean.class);

        System.out.println("INITIALIZED=" + ExampleState.initialized);
        System.out.println("PROXY_CREATED=" + Proxy.isProxyClass(appService.getClass()));
        System.out.println("SERVICE_RESULT=" + appService.serve());
        System.out.println("INTERCEPTED=" + ExampleState.intercepted);
        System.out.println("PROTOTYPE_NEW_INSTANCE=" + (first != second));
        context.close();
        System.out.println("DESTROYED=" + ExampleState.destroyed);
        System.out.println("PHASE-2-HAPPY-PATH: PASS");
    }
}
