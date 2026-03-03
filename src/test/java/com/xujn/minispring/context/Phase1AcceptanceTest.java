package com.xujn.minispring.context;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeanCurrentlyInCreationException;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.exception.NoSuchBeanDefinitionException;
import com.xujn.minispring.test.phase1.ambiguity.SomeService;
import com.xujn.minispring.test.phase1.bean.OrderService;
import com.xujn.minispring.test.phase1.bean.SimpleComponent;
import com.xujn.minispring.test.phase1.bean.UserRepository;
import com.xujn.minispring.test.phase1.bean.UserService;
import com.xujn.minispring.test.phase1.config.Phase1ScanConfig;
import com.xujn.minispring.test.phase1.cycle.direct.CircularA;
import com.xujn.minispring.test.phase1.cycle.indirect.CycleA;
import com.xujn.minispring.test.phase1.cycle.self.SelfDependent;
import com.xujn.minispring.test.phase1.interfaceinject.JpaUserRepository;
import com.xujn.minispring.test.phase1.multilevel.ServiceA;
import com.xujn.minispring.test.phase1.multilevel.ServiceB;
import com.xujn.minispring.test.phase1.multilevel.ServiceC;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase1AcceptanceTest {

    @Test
    void tc11_scanSinglePackageRegistersOnlyComponents() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        assertEquals(4, context.getBeanDefinitionCount());
        assertTrue(Arrays.asList(context.getBeanDefinitionNames()).contains("userRepository"));
        assertTrue(Arrays.asList(context.getBeanDefinitionNames()).contains("userService"));
        assertTrue(Arrays.asList(context.getBeanDefinitionNames()).contains("orderService"));
        assertTrue(Arrays.asList(context.getBeanDefinitionNames()).contains("simpleComponent"));
        assertFalse(Arrays.asList(context.getBeanDefinitionNames()).contains("plainHelper"));
    }

    @Test
    void tc12_defaultBeanNameUsesDecapitalize() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        assertTrue(context.containsBeanDefinition("userService"));
    }

    @Test
    void tc13_emptyPackageStartsNormally() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.empty");

        assertEquals(0, context.getBeanDefinitionCount());
    }

    @Test
    void tc14_beanDefinitionMetadataShouldMatchAnnotations() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        assertEquals(OrderService.class, context.getBeanDefinition("orderService").getBeanClass());
        assertEquals("singleton", context.getBeanDefinition("orderService").getScope());
        assertTrue(context.getBeanDefinition("orderService").isSingleton());
    }

    @Test
    void tc21_getBeanByName() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        Object bean = context.getBean("userService");

        assertNotNull(bean);
        assertTrue(bean instanceof UserService);
    }

    @Test
    void tc22_getBeanByType() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        UserService bean = context.getBean(UserService.class);

        assertNotNull(bean);
    }

    @Test
    void tc23_getBeanByNameAndType() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        UserService bean = context.getBean("userService", UserService.class);

        assertNotNull(bean);
    }

    @Test
    void tc24_missingBeanByNameThrows() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        NoSuchBeanDefinitionException exception =
                assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean("nonExistent"));

        assertTrue(exception.getMessage().contains("nonExistent"));
    }

    @Test
    void tc25_missingBeanByTypeThrows() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(PaymentService.class));
    }

    @Test
    void tc31_singletonShouldReturnSameInstance() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        UserService first = context.getBean(UserService.class);
        UserService second = context.getBean(UserService.class);

        assertSame(first, second);
    }

    @Test
    void tc32_containsBeanReturnsTrue() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        assertTrue(context.containsBean("userService"));
    }

    @Test
    void tc41_singleLevelAutowiredInjection() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        UserService userService = context.getBean(UserService.class);
        UserRepository userRepository = context.getBean(UserRepository.class);

        assertNotNull(userService.getUserRepository());
        assertSame(userRepository, userService.getUserRepository());
    }

    @Test
    void tc42_multiLevelAutowiredInjection() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.multilevel");

        ServiceA serviceA = context.getBean(ServiceA.class);

        assertNotNull(serviceA.getServiceB());
        assertNotNull(serviceA.getServiceB().getServiceC());
        assertSame(context.getBean(ServiceB.class), serviceA.getServiceB());
        assertSame(context.getBean(ServiceC.class), serviceA.getServiceB().getServiceC());
    }

    @Test
    void tc43_simpleComponentCreatesSuccessfully() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        assertNotNull(context.getBean(SimpleComponent.class));
    }

    @Test
    void tc44_ambiguousTypeInjectionFailsFast() {
        BeansException exception = assertThrows(BeansException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.ambiguity")
                        .getBean(SomeService.class));

        assertTrue(exception.getMessage().contains("com.xujn.minispring.test.phase1.ambiguity.DataSource"));
        assertTrue(exception.getMessage().contains("2"));
    }

    @Test
    void tc51_directCircularDependencyFails() {
        BeanCurrentlyInCreationException exception = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.cycle.direct"));

        assertTrue(exception.getMessage().contains("circularA -> circularB -> circularA")
                || exception.getMessage().contains("circularB -> circularA -> circularB"));
    }

    @Test
    void tc52_indirectCircularDependencyFails() {
        BeanCurrentlyInCreationException exception = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.cycle.indirect"));

        assertTrue(exception.getMessage().contains("cycleA -> cycleB -> cycleC -> cycleA"));
    }

    @Test
    void tc53_selfDependencyFails() {
        BeanCurrentlyInCreationException exception = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.cycle.self"));

        assertTrue(exception.getMessage().contains("selfDependent -> selfDependent"));
    }

    @Test
    void tc54_nonCircularMultiDependencyStartsNormally() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.multilevel");

        assertDoesNotThrow(() -> context.getBean(ServiceA.class));
        assertNotNull(context.getBean(ServiceA.class).getServiceB());
        assertNotNull(context.getBean(ServiceA.class).getServiceB().getServiceC());
    }

    @Test
    void tc61_missingBeanMessageContainsBeanName() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        NoSuchBeanDefinitionException exception =
                assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean("fooBar"));

        assertTrue(exception.getMessage().contains("fooBar"));
    }

    @Test
    void tc62_circularDependencyMessageContainsBeanPath() {
        BeanCurrentlyInCreationException exception = assertThrows(BeanCurrentlyInCreationException.class,
                () -> new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.cycle.direct"));

        assertTrue(exception.getMessage().contains("circularA -> circularB -> circularA")
                || exception.getMessage().contains("circularB -> circularA -> circularB"));
    }

    @Test
    void tc71_refreshSecondCallFailsFast() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.bean");

        BeansException exception = assertThrows(BeansException.class, context::refresh);

        assertTrue(exception.getMessage().contains("already been refreshed"));
    }

    @Test
    void tc72_contextWithoutPackagesStartsEmpty() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        assertEquals(0, context.getBeanDefinitionCount());
    }

    @Test
    void tc73_interfaceFieldInjectsSingleImplementation() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.phase1.interfaceinject");

        com.xujn.minispring.test.phase1.interfaceinject.UserService userService =
                context.getBean(com.xujn.minispring.test.phase1.interfaceinject.UserService.class);

        assertNotNull(userService.getRepo());
        assertTrue(userService.getRepo() instanceof JpaUserRepository);
    }

    @Test
    void configClassConstructorShouldResolveComponentScanPackages() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Phase1ScanConfig.class);

        assertTrue(context.containsBeanDefinition("userService"));
    }

    interface PaymentService {
    }
}
