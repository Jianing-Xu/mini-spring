package com.xujn.minispring.test.javaconfig.phase3.postprocessor;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanFactoryPostProcessor;
import com.xujn.minispring.context.annotation.Bean;
import com.xujn.minispring.context.annotation.ComponentScan;
import com.xujn.minispring.context.annotation.Configuration;

@Configuration
@ComponentScan("com.xujn.minispring.test.javaconfig.phase3.postprocessor")
public class PostProcessorConfig {

    @Bean
    public BeanFactoryPostProcessor markerRegisteringPostProcessor() {
        return registry -> {
            if (registry.containsBeanDefinition("postProcessedMarker")) {
                return;
            }
            BeanDefinition beanDefinition =
                    new BeanDefinition(PostProcessedMarker.class, "postProcessedMarker");
            beanDefinition.setSource("JavaConfigPhase3:markerRegisteringPostProcessor");
            registry.registerBeanDefinition("postProcessedMarker", beanDefinition);
        };
    }
}
