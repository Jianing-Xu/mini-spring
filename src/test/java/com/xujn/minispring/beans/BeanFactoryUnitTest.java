package com.xujn.minispring.beans;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.context.annotation.Scope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanFactoryUnitTest {

    @Test
    void beanDefinitionMetadataShouldBeCorrect() {
        BeanDefinition beanDefinition = new BeanDefinition(OrderService.class, "orderService");
        beanDefinition.setScope("singleton");

        assertEquals(OrderService.class, beanDefinition.getBeanClass());
        assertEquals("orderService", beanDefinition.getBeanName());
        assertEquals("singleton", beanDefinition.getScope());
        assertTrue(beanDefinition.isSingleton());
    }

    @Test
    void singletonBeanLookupShouldReturnSameInstance() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("userRepository",
                new BeanDefinition(UserRepository.class, "userRepository"));

        UserRepository first = beanFactory.getBean("userRepository", UserRepository.class);
        UserRepository second = beanFactory.getBean(UserRepository.class);

        assertSame(first, second);
    }

    @Test
    void getBeanByTypeShouldFailWhenMultipleCandidatesExist() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("mysqlDataSource",
                new BeanDefinition(MysqlDataSource.class, "mysqlDataSource"));
        beanFactory.registerBeanDefinition("h2DataSource",
                new BeanDefinition(H2DataSource.class, "h2DataSource"));

        BeansException exception = assertThrows(BeansException.class, () -> beanFactory.getBean(DataSource.class));

        assertTrue(exception.getMessage().contains(DataSource.class.getName()));
        assertTrue(exception.getMessage().contains("2"));
        assertTrue(exception.getMessage().contains("mysqlDataSource"));
        assertTrue(exception.getMessage().contains("h2DataSource"));
    }

    @Component
    static class UserRepository {
    }

    @Component
    @Scope("singleton")
    static class OrderService {
    }

    interface DataSource {
    }

    @Component
    static class MysqlDataSource implements DataSource {
    }

    @Component
    static class H2DataSource implements DataSource {
    }
}
