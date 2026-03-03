package com.xujn.minispring.context;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.test.phase2.aop.interceptor.LoggingInterceptor;
import com.xujn.minispring.test.phase2.aop.service.ConsumerService;
import com.xujn.minispring.test.phase2.aop.service.NoInterfaceBean;
import com.xujn.minispring.test.phase2.aop.service.TestService;
import com.xujn.minispring.test.phase2.aop.service.TestServiceImpl;
import com.xujn.minispring.test.phase2.bpp.BppA;
import com.xujn.minispring.test.phase2.bpp.BppB;
import com.xujn.minispring.test.phase2.bpp.BusinessBeanOne;
import com.xujn.minispring.test.phase2.bpp.EarlyBpp;
import com.xujn.minispring.test.phase2.bpp.ReplaceableService;
import com.xujn.minispring.test.phase2.bpp.TrackingBpp;
import com.xujn.minispring.test.phase2.bpp.TrackingState;
import com.xujn.minispring.test.phase2.lifecycle.aop.AopLifecycleService;
import com.xujn.minispring.test.phase2.lifecycle.aop.AopLifecycleState;
import com.xujn.minispring.test.phase2.lifecycle.destroy.DisposableService;
import com.xujn.minispring.test.phase2.lifecycle.destroy.DisposableState;
import com.xujn.minispring.test.phase2.lifecycle.destroy.NormalDisposable;
import com.xujn.minispring.test.phase2.lifecycle.destroy.PrototypeDisposable;
import com.xujn.minispring.test.phase2.lifecycle.init.LifecycleBean;
import com.xujn.minispring.test.phase2.lifecycle.init.LifecycleEvents;
import com.xujn.minispring.test.phase2.lifecycle.init.SimpleBean;
import com.xujn.minispring.test.phase2.lifecycle.init.TrackingBeforeInitBpp;
import com.xujn.minispring.test.phase2.prototype.PrototypeBean;
import com.xujn.minispring.test.phase2.prototype.SingletonDep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase2AcceptanceTest {

    @BeforeEach
    void setUp() {
        LifecycleBean.reset();
        TrackingBeforeInitBpp.reset();
        LifecycleEvents.reset();
        DisposableState.reset();
        TrackingState.reset();
        EarlyBpp.reset();
        LoggingInterceptor.reset();
        AopLifecycleState.reset();
    }

    @Test
    void shouldInvokeAfterPropertiesSetAfterDependencyInjectionAndBeforeAfterBpp() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase2.lifecycle.init");

        LifecycleBean bean = context.getBean(LifecycleBean.class);

        assertTrue(bean.isInitialized());
        assertTrue(bean.isDependencyVisibleDuringInit());
        assertTrue(TrackingBeforeInitBpp.getBeforeInitTime() < LifecycleBean.getAfterPropertiesSetTime());
        assertEquals(List.of("populateBean", "bppBefore", "afterPropertiesSet", "bppAfter"), LifecycleEvents.EVENTS);
    }

    @Test
    void shouldAllowBeansWithoutInitializingBean() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase2.lifecycle.init");

        assertNotNull(context.getBean(SimpleBean.class));
    }

    @Test
    void shouldDestroySingletonBeansOnCloseAndIgnorePrototype() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase2.lifecycle.destroy");
        DisposableService disposableService = context.getBean(DisposableService.class);
        PrototypeDisposable prototypeDisposable = context.getBean(PrototypeDisposable.class);

        assertNotNull(disposableService);
        assertNotNull(prototypeDisposable);
        assertDoesNotThrow(context::close);
        assertTrue(DisposableState.disposableDestroyed);
        assertTrue(DisposableState.secondDestroyed);
        assertTrue(DisposableState.thirdDestroyed);
        assertTrue(DisposableState.normalDestroyed);
        assertFalse(DisposableState.prototypeDestroyed);
    }

    @Test
    void shouldExecuteBeanPostProcessorsAndRespectOrder() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase2.bpp");

        assertNotNull(context.getBean(BusinessBeanOne.class));
        assertTrue(TrackingState.BEFORE.containsAll(List.of("businessBeanOne", "businessBeanTwo", "businessBeanThree")));
        assertTrue(TrackingState.AFTER.containsAll(List.of("businessBeanOne", "businessBeanTwo", "businessBeanThree")));
        assertTrue(TrackingState.ORDER.indexOf("BPP_A.before:businessBeanOne")
                < TrackingState.ORDER.indexOf("BPP_B.before:businessBeanOne"));
        assertTrue(TrackingState.ORDER.indexOf("BPP_A.after:businessBeanOne")
                < TrackingState.ORDER.indexOf("BPP_B.after:businessBeanOne"));
        ReplaceableService replaceableService = context.getBean("replaceableServiceImpl", ReplaceableService.class);
        assertEquals("wrapped", replaceableService.value());
        assertTrue(EarlyBpp.isBusinessBeanProcessed());
        assertEquals(6, context.getBeanFactory().getBeanPostProcessorCount());
        assertNotNull(context.getBean(TrackingBpp.class));
        assertNotNull(context.getBean(BppA.class));
        assertNotNull(context.getBean(BppB.class));
    }

    @Test
    void shouldCreateProxyAndInvokeInterceptor() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        "com.xujn.minispring.test.phase2.aop.logging",
                        "com.xujn.minispring.test.phase2.aop.service",
                        "com.xujn.minispring.test.phase2.aop.other"
                );

        TestService testService = context.getBean(TestService.class);
        ConsumerService consumerService = context.getBean(ConsumerService.class);
        NoInterfaceBean noInterfaceBean = context.getBean(NoInterfaceBean.class);

        assertTrue(Proxy.isProxyClass(testService.getClass()));
        assertFalse(Proxy.isProxyClass(context.getBean(com.xujn.minispring.test.phase2.aop.other.OtherBean.class).getClass()));
        assertEquals("result", testService.doSomething());
        assertTrue(LoggingInterceptor.isIntercepted());
        assertFalse(Proxy.isProxyClass(noInterfaceBean.getClass()));
        assertSame(testService, consumerService.getTestService());
    }

    @Test
    void shouldAllowInterceptorToModifyReturnValue() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        "com.xujn.minispring.test.phase2.aop.service",
                        "com.xujn.minispring.test.phase2.aop.logging"
                );

        assertEquals("result", context.getBean(TestService.class).doSomething());

        AnnotationConfigApplicationContext modifyingContext =
                new AnnotationConfigApplicationContext(
                        "com.xujn.minispring.test.phase2.aop.service",
                        "com.xujn.minispring.test.phase2.aop.mod"
                );

        assertEquals("result_modified", modifyingContext.getBean(TestService.class).doSomething());
    }

    @Test
    void shouldCreatePrototypeBeansWithoutCaching() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase2.prototype");

        PrototypeBean first = context.getBean(PrototypeBean.class);
        PrototypeBean second = context.getBean(PrototypeBean.class);

        assertNotSame(first, second);
        assertFalse(context.getBeanFactory().containsSingleton("prototypeBean"));
        assertNotNull(first.getDep());
        assertSame(context.getBean(SingletonDep.class), first.getDep());
    }

    @Test
    void shouldSupportLifecycleAndAopTogether() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        "com.xujn.minispring.test.phase2.lifecycle.aop"
                );

        AopLifecycleService service = context.getBean(AopLifecycleService.class);

        assertTrue(AopLifecycleState.initialized);
        assertTrue(Proxy.isProxyClass(service.getClass()));
        assertEquals("lifecycle", service.work());
        assertTrue(LoggingInterceptor.isIntercepted());
        context.close();
        assertTrue(AopLifecycleState.destroyed);
    }

    @Test
    void phase1RegressionShouldStillPassForCircularDependency() {
        BeansException exception = org.junit.jupiter.api.Assertions.assertThrows(BeansException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.cycle.direct"));
        assertTrue(exception.getMessage().contains("circular"));
    }
}
