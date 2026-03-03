package com.xujn.minispring.context;

import com.xujn.minispring.aop.framework.autoproxy.AutoProxyCreator;
import com.xujn.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeanCurrentlyInCreationException;
import com.xujn.minispring.test.phase3.aop.Phase3LoggingInterceptor;
import com.xujn.minispring.test.phase3.aop.dual.DualSideAutoProxyCreator;
import com.xujn.minispring.test.phase3.aop.dual.InterfaceA;
import com.xujn.minispring.test.phase3.aop.dual.InterfaceB;
import com.xujn.minispring.test.phase3.aop.single.ProxiedA;
import com.xujn.minispring.test.phase3.aop.single.ProxiedB;
import com.xujn.minispring.test.phase3.aop.single.SingleSideAutoProxyCreator;
import com.xujn.minispring.test.phase3.circular.CircularA;
import com.xujn.minispring.test.phase3.circular.CircularB;
import com.xujn.minispring.test.phase3.circular.CycleA;
import com.xujn.minispring.test.phase3.circular.SelfRefBean;
import com.xujn.minispring.test.phase3.circular.ServiceA;
import com.xujn.minispring.test.phase3.circular.ServiceB;
import com.xujn.minispring.test.phase3.circular.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase3AcceptanceTest {

    @BeforeEach
    void reset() {
        Phase3LoggingInterceptor.reset();
    }

    @Test
    void shouldResolveDirectAndIndirectSingletonFieldCycles() {
        AnnotationConfigApplicationContext directContext =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.circular");
        CircularA a = directContext.getBean(CircularA.class);
        CircularB b = directContext.getBean(CircularB.class);

        assertSame(b, a.getB());
        assertSame(a, b.getA());

        CycleA cycleA = directContext.getBean(CycleA.class);
        assertSame(cycleA, cycleA.getB().getC().getA());
    }

    @Test
    void shouldResolveSelfReferenceAndKeepNonCircularBeansStable() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.circular");
        SelfRefBean selfRefBean = context.getBean(SelfRefBean.class);
        ServiceA serviceA = context.getBean(ServiceA.class);
        DefaultListableBeanFactory beanFactory = context.getBeanFactory();

        assertSame(selfRefBean, selfRefBean.getSelf());
        assertSame(context.getBean(ServiceB.class), serviceA.getB());
        assertFalse(beanFactory.containsSingletonFactory("serviceB"));
        assertTrue(beanFactory.getEarlySingletonObjectsSnapshot().isEmpty());
        assertTrue(beanFactory.getSingletonFactoriesSnapshot().isEmpty());
    }

    @Test
    void shouldProvideSameProxyForCircularDependencySingleSide() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.aop.single");
        ProxiedA proxiedA = context.getBean(ProxiedA.class);
        ProxiedB proxiedB = context.getBean(ProxiedB.class);
        SingleSideAutoProxyCreator autoProxyCreator = context.getBean(SingleSideAutoProxyCreator.class);

        assertTrue(Proxy.isProxyClass(proxiedB.getClass()));
        assertTrue(Proxy.isProxyClass(proxiedA.getB().getClass()));
        assertSame(proxiedB, proxiedA.getB());
        assertEquals("proxiedB", proxiedA.getB().doSomething());
        assertTrue(Phase3LoggingInterceptor.isIntercepted());
        assertEquals(1, autoProxyCreator.getEarlyReferenceCount("proxiedBImpl"));
    }

    @Test
    void shouldProvideSameProxyForCircularDependencyDualSide() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.aop.dual");
        InterfaceA a = context.getBean(InterfaceA.class);
        InterfaceB b = context.getBean(InterfaceB.class);
        DualSideAutoProxyCreator autoProxyCreator = context.getBean(DualSideAutoProxyCreator.class);

        assertTrue(Proxy.isProxyClass(a.getClass()));
        assertTrue(Proxy.isProxyClass(b.getClass()));
        assertSame(b, a.getB());
        assertSame(a, b.getA());
        int earlyA = autoProxyCreator.getEarlyReferenceCount("dualProxyAImpl");
        int earlyB = autoProxyCreator.getEarlyReferenceCount("dualProxyBImpl");
        assertTrue((earlyA == 1 && earlyB == 0) || (earlyA == 0 && earlyB == 1));
    }

    @Test
    void shouldFailFastForUnsupportedConstructorAndPrototypeCycles() {
        BeanCurrentlyInCreationException ctorException = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.unsupported.ctor"));
        assertTrue(ctorException.getMessage().contains("constructor injection circular dependency is not supported")
                || ctorException.getMessage().contains("currently in creation"));

        BeanCurrentlyInCreationException prototypeException = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.unsupported.prototype"));
        assertTrue(prototypeException.getMessage().contains("prototype circular dependency is not supported"));

        BeanCurrentlyInCreationException mixException = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.unsupported.mix"));
        assertTrue(mixException.getMessage().contains("constructor injection circular dependency is not supported"));
    }

    @Test
    void shouldClearThreeLevelCachesAfterResolution() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.circular");
        DefaultListableBeanFactory beanFactory = context.getBeanFactory();

        assertTrue(beanFactory.getSingletonObjectsSnapshot().containsKey("circularA"));
        assertTrue(beanFactory.getSingletonObjectsSnapshot().containsKey("circularB"));
        assertTrue(beanFactory.getEarlySingletonObjectsSnapshot().isEmpty());
        assertTrue(beanFactory.getSingletonFactoriesSnapshot().isEmpty());
        assertTrue(beanFactory.getSingletonObjectsSnapshot().containsKey("simpleBean"));
    }

    @Test
    void shouldKeepLargeNoCycleStartupWithoutEarlyCaches() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.circular");
        DefaultListableBeanFactory beanFactory = context.getBeanFactory();

        assertNotNull(context.getBean(SimpleBean.class));
        assertDoesNotThrow(() -> context.getBean("loadBean20"));
        assertTrue(beanFactory.getEarlySingletonObjectsSnapshot().isEmpty());
        assertTrue(beanFactory.getSingletonFactoriesSnapshot().isEmpty());
    }

    @Test
    void shouldReturnSameSingletonOnRepeatedLookups() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase3.circular");

        CircularA first = context.getBean(CircularA.class);
        for (int i = 0; i < 5; i++) {
            assertSame(first, context.getBean(CircularA.class));
        }
    }
}
