package com.xujn.minispring.context;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.exception.NoSuchBeanDefinitionException;
import com.xujn.minispring.test.javaconfig.phase1.basic.AppConfig;
import com.xujn.minispring.test.javaconfig.phase1.basic.DataSource;
import com.xujn.minispring.test.javaconfig.phase1.basic.ExtraService;
import com.xujn.minispring.test.javaconfig.phase1.basic.NamedDataSource;
import com.xujn.minispring.test.javaconfig.phase1.basic.ServiceA;
import com.xujn.minispring.test.javaconfig.phase1.basic.ServiceB;
import com.xujn.minispring.test.javaconfig.phase1.basic.ServiceC;
import com.xujn.minispring.test.javaconfig.phase1.coexist.MyService;
import com.xujn.minispring.test.javaconfig.phase1.coexist.UserRepository;
import com.xujn.minispring.test.javaconfig.phase1.coexist.UserService;
import com.xujn.minispring.test.javaconfig.phase1.lifecycle.LifecycleService;
import com.xujn.minispring.test.javaconfig.phase1.lifecycle.LifecycleState;
import com.xujn.minispring.test.javaconfig.phase1.noconfig.OnlyComponent;
import com.xujn.minispring.test.javaconfig.phase1.voidcase.ValidBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaConfigPhase1AcceptanceTest {

    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        originalErr = System.err;
        LifecycleState.reset();
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    void shouldRegisterConfigurationClassAsComponentSingletonAndIgnorePlainComponentBeanMethods() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.basic");

        assertTrue(context.containsBeanDefinition("appConfig"));
        assertSame(context.getBean(AppConfig.class), context.getBean(AppConfig.class));
        assertFalse(context.containsBeanDefinition("ignoredBean"));
    }

    @Test
    void shouldRegisterBeanMethodDefinitionsAcrossMultipleConfigurations() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.basic");

        assertTrue(context.containsBeanDefinition("dataSource"));
        assertTrue(context.containsBeanDefinition("myDs"));
        assertTrue(context.containsBeanDefinition("serviceA"));
        assertTrue(context.containsBeanDefinition("serviceC"));
        assertTrue(context.containsBeanDefinition("extraService"));
        assertFalse(context.containsBeanDefinition("helper"));

        BeanDefinition dataSourceDefinition = context.getBeanDefinition("dataSource");
        assertEquals("appConfig", dataSourceDefinition.getFactoryBeanName());
        assertEquals("dataSource", dataSourceDefinition.getFactoryMethodName());

        assertNotNull(context.getBean("dataSource", DataSource.class));
        assertNotNull(context.getBean("myDs", NamedDataSource.class));
        assertNotNull(context.getBean(ServiceC.class));
        assertNotNull(context.getBean(ExtraService.class));
    }

    @Test
    void shouldCreateSingletonFactoryMethodBeansAndInjectAutowiredFields() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.basic");

        ServiceA first = context.getBean(ServiceA.class);
        ServiceA second = context.getBean(ServiceA.class);

        assertSame(first, second);
        assertNotNull(first.getServiceB());
        assertSame(context.getBean(ServiceB.class), first.getServiceB());
    }

    @Test
    void shouldApplyLifecycleAndBeanPostProcessorsToFactoryMethodBeans() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.lifecycle");

        LifecycleService lifecycleService = context.getBean(LifecycleService.class);

        assertTrue(lifecycleService.isInitialized());
        assertTrue(LifecycleState.dependencyVisibleDuringInit);
        assertTrue(LifecycleState.beforeInitializationCalled);
        assertTrue(LifecycleState.afterInitializationCalled);
    }

    @Test
    void shouldAllowAnnotationScanAndJavaConfigBeansToCoexistAndInjectEachOther() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.coexist");

        UserService userService = context.getBean(UserService.class);
        MyService myService = context.getBean(MyService.class);

        assertNotNull(context.getBean(com.xujn.minispring.test.javaconfig.phase1.coexist.DataSource.class));
        assertNotNull(userService.getDataSource());
        assertSame(context.getBean(com.xujn.minispring.test.javaconfig.phase1.coexist.DataSource.class),
                userService.getDataSource());
        assertNotNull(myService.getUserRepository());
        assertSame(context.getBean(UserRepository.class), myService.getUserRepository());
    }

    @Test
    void shouldRunConfigurationClassPostProcessorAndNoOpWithoutConfigurations() {
        AnnotationConfigApplicationContext configContext =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.basic");
        AnnotationConfigApplicationContext noConfigContext =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.noconfig");

        assertTrue(configContext.containsBeanDefinition("serviceA"));
        assertNotNull(noConfigContext.getBean(OnlyComponent.class));
    }

    @Test
    void shouldSkipVoidBeanMethods() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.voidcase");

        assertFalse(context.containsBeanDefinition("invalidBean"));
        assertNotNull(context.getBean(ValidBean.class));
    }

    @Test
    void shouldFailWhenBeanMethodReturnsNull() {
        BeansException exception = assertThrows(BeansException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.nullcase"));

        assertTrue(exception.getMessage().contains("@Bean method returned null"));
        assertTrue(exception.getMessage().contains("nullBean"));
    }

    @Test
    void shouldFailWhenFactoryMethodInvocationThrows() {
        BeansException exception = assertThrows(BeansException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.throwing"));

        assertTrue(exception.getMessage().contains("failingBean"));
        assertTrue(exception.getMessage().contains("init failed"));
    }

    @Test
    void shouldFailWhenFactoryBeanDefinitionIsMissing() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition beanDefinition = new BeanDefinition(DataSource.class, "brokenDataSource");
        beanDefinition.setFactoryBeanName("missingConfig");
        beanDefinition.setFactoryMethodName("dataSource");
        beanFactory.registerBeanDefinition("brokenDataSource", beanDefinition);

        NoSuchBeanDefinitionException exception = assertThrows(NoSuchBeanDefinitionException.class,
                () -> beanFactory.getBean("brokenDataSource"));

        assertTrue(exception.getMessage().contains("missingConfig"));
    }

    @Test
    void shouldPrintWarningForNonEnhancedConfigurationClasses() {
        ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errBuffer));

        new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase1.basic");

        String errOutput = errBuffer.toString();
        assertTrue(errOutput.contains("WARN"));
        assertTrue(errOutput.contains("not CGLIB-enhanced"));
        assertTrue(errOutput.contains(AppConfig.class.getName()));
    }
}
