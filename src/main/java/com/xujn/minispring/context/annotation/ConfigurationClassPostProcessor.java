package com.xujn.minispring.context.annotation;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanDefinitionRegistry;
import com.xujn.minispring.beans.factory.config.BeanFactoryPostProcessor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Internal post-processor that expands {@link Configuration} classes into {@link Bean} definitions.
 * Constraint: runs before singleton pre-instantiation and emits warnings about missing CGLIB enhancement.
 * Thread-safety: bootstrap-only component, not designed for concurrent invocation.
 */
public class ConfigurationClassPostProcessor implements BeanFactoryPostProcessor {

    private final ConfigurationClassParser parser = new ConfigurationClassParser();
    private final ConfigurationClassBeanDefinitionReader reader = new ConfigurationClassBeanDefinitionReader();

    @Override
    public void postProcessBeanFactory(BeanDefinitionRegistry registry) {
        Set<BeanDefinition> configCandidates = new LinkedHashSet<>();
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (parser.isConfigurationClass(beanDefinition)) {
                configCandidates.add(beanDefinition);
            }
        }
        if (configCandidates.isEmpty()) {
            return;
        }
        Set<ConfigurationClass> configurationClasses = parser.parse(configCandidates);
        reader.loadBeanDefinitions(configurationClasses, registry);
        for (ConfigurationClass configurationClass : configurationClasses) {
            // Without CGLIB enhancement, direct @Bean inter-method calls bypass the container singleton cache.
            System.err.println("WARN @Configuration class [" + configurationClass.getConfigClass().getName() +
                    "] is not CGLIB-enhanced; use container-managed dependencies instead of inter-method calls");
        }
    }
}
