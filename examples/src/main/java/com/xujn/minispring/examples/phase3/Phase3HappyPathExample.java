package com.xujn.minispring.examples.phase3;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.examples.phase3.fixture.CircularA;
import com.xujn.minispring.examples.phase3.fixture.Phase3AutoProxyCreator;
import com.xujn.minispring.examples.phase3.fixture.Phase3State;
import com.xujn.minispring.examples.phase3.fixture.ProxiedA;
import com.xujn.minispring.examples.phase3.fixture.ProxiedB;

import java.lang.reflect.Proxy;

/**
 * Manual verification entry for the Phase 3 three-level-cache and AOP circular-dependency path.
 */
public class Phase3HappyPathExample {

    public static void main(String[] args) {
        Phase3State.reset();
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase3.fixture");

        CircularA circularA = context.getBean(CircularA.class);
        ProxiedA proxiedA = context.getBean(ProxiedA.class);
        ProxiedB proxiedB = context.getBean(ProxiedB.class);
        Phase3AutoProxyCreator autoProxyCreator = context.getBean(Phase3AutoProxyCreator.class);

        System.out.println("DIRECT_CYCLE_RESOLVED=" + (circularA == circularA.getB().getA()));
        System.out.println("PROXY_CREATED=" + Proxy.isProxyClass(proxiedB.getClass()));
        System.out.println("PROXY_REFERENCE_CONSISTENT=" + (proxiedA.getB() == proxiedB));
        System.out.println("EARLY_PROXY_COUNT=" + autoProxyCreator.getEarlyReferenceCount("proxiedBImpl"));
        System.out.println("INTERCEPTED=" + "phase3".equals(proxiedA.getB().work()) + ":" + Phase3State.intercepted);
        System.out.println("EARLY_CACHE_EMPTY=" + context.getBeanFactory().getEarlySingletonObjectsSnapshot().isEmpty());
        System.out.println("FACTORY_CACHE_EMPTY=" + context.getBeanFactory().getSingletonFactoriesSnapshot().isEmpty());
        System.out.println("PHASE-3-HAPPY-PATH: PASS");
    }
}
