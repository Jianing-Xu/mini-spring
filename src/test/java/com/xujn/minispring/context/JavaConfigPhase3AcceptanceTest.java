package com.xujn.minispring.context;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.test.javaconfig.phase3.postprocessor.PostProcessedMarker;
import org.junit.jupiter.api.Test;

class JavaConfigPhase3AcceptanceTest {

    @Test
    void shouldInvokeCustomBeanFactoryPostProcessorsDiscoveredFromContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.javaconfig.phase3.postprocessor");

        PostProcessedMarker marker = context.getBean(PostProcessedMarker.class);

        assertNotNull(marker);
        assertTrue(context.containsBeanDefinition("postProcessedMarker"));
    }
}
