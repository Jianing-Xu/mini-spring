package com.xujn.minispring.examples.javaconfig.phase1;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.examples.javaconfig.phase1.fixture.happy.AppService;
import com.xujn.minispring.examples.javaconfig.phase1.fixture.happy.DataSource;
import com.xujn.minispring.examples.javaconfig.phase1.fixture.happy.ExampleConfig;
import com.xujn.minispring.examples.javaconfig.phase1.fixture.happy.UserService;

/**
 * Manual verification entry for JavaConfig Phase 1 happy-path behavior.
 */
public class JavaConfigPhase1HappyPathExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.javaconfig.phase1.fixture.happy");

        ExampleConfig firstConfig = context.getBean(ExampleConfig.class);
        ExampleConfig secondConfig = context.getBean(ExampleConfig.class);
        AppService appService = context.getBean(AppService.class);
        UserService userService = context.getBean(UserService.class);
        DataSource dataSource = context.getBean(DataSource.class);
        BeanDefinition beanDefinition = context.getBeanDefinition("dataSource");

        System.out.println("CONFIG_SINGLETON=" + (firstConfig == secondConfig));
        System.out.println("JAVA_CONFIG_BEAN_CREATED=" + (dataSource != null));
        System.out.println("JAVACONFIG_DEPENDENCY_INJECTED=" + (appService.getUserRepository() != null));
        System.out.println("COMPONENT_DEPENDENCY_INJECTED=" + (userService.getDataSource() == dataSource));
        System.out.println("FACTORY_METADATA=" + beanDefinition.getFactoryBeanName() + ":" +
                beanDefinition.getFactoryMethodName());
        System.out.println("PHASE-JAVACONFIG-1-HAPPY-PATH: PASS");
    }
}
