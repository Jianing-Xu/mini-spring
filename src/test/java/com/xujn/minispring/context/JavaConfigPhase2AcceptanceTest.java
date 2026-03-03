package com.xujn.minispring.context;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeanDefinitionOverrideException;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.exception.NoSuchBeanDefinitionException;
import com.xujn.minispring.test.javaconfig.phase2.lifecycle.LifecyclePhase2State;
import com.xujn.minispring.test.javaconfig.phase2.lifecycle.ManagedLifecycleBean;
import com.xujn.minispring.test.javaconfig.phase2.params.ConfigCreatedService;
import com.xujn.minispring.test.javaconfig.phase2.params.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaConfigPhase2AcceptanceTest {

    @BeforeEach
    void setUp() {
        LifecyclePhase2State.reset();
    }

    @Test
    void shouldResolveBeanMethodParametersByType() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase2.params");

        ConfigCreatedService service = context.getBean(ConfigCreatedService.class);

        assertTrue(service.isReady());
        assertSame(context.getBean(Repository.class), service.getRepository());
    }

    @Test
    void shouldFailWhenBeanMethodParameterTypeIsMissing() {
        BeansException exception = assertThrows(BeansException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase2.missing"));

        assertTrue(exception.getMessage().contains("missingService"));
        assertTrue(exception.getMessage().contains("MissingDependency"));
        assertTrue(findRootCause(exception) instanceof NoSuchBeanDefinitionException);
    }

    @Test
    void shouldFailWhenBeanMethodParameterTypeIsAmbiguous() {
        BeansException exception = assertThrows(BeansException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase2.ambiguous"));

        assertTrue(exception.getMessage().contains("ambiguousService"));
        assertTrue(exception.getMessage().contains("Client"));
    }

    @Test
    void shouldFailFastForDuplicateBeanNamesAcrossSources() {
        BeanDefinitionOverrideException exception = assertThrows(BeanDefinitionOverrideException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase2.conflict"));

        assertTrue(exception.getMessage().contains("duplicateService"));
        assertTrue(exception.getMessage().contains("AnnotationScan"));
        assertTrue(exception.getMessage().contains("JavaConfig"));
    }

    @Test
    void shouldInvokeCustomInitAndDestroyMethodsForBeanMethods() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase2.lifecycle");

        ManagedLifecycleBean bean = context.getBean(ManagedLifecycleBean.class);

        assertTrue(bean.isAfterPropertiesSetCalled());
        assertTrue(LifecyclePhase2State.customInitCalled);
        context.close();
        assertTrue(LifecyclePhase2State.interfaceDestroyCalled);
        assertTrue(LifecyclePhase2State.customDestroyCalled);
    }

    private Throwable findRootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }
}
