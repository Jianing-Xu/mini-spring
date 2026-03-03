package com.xujn.minispring.examples.phase1;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeanCurrentlyInCreationException;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.exception.NoSuchBeanDefinitionException;
import com.xujn.minispring.examples.phase1.fixture.failure.ambiguity.SomeService;

/**
 * Manual verification entry for the Phase 1 failure path.
 */
public class Phase1FailurePathExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext baseContext =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.happy");
        try {
            baseContext.getBean("missingBean");
        } catch (NoSuchBeanDefinitionException ex) {
            System.out.println("MISSING_BEAN=" + ex.getMessage());
        }

        try {
            new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.failure.ambiguity")
                    .getBean(SomeService.class);
        } catch (BeansException ex) {
            System.out.println("AMBIGUOUS_TYPE=" + ex.getMessage());
        }

        try {
            new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.failure.direct");
        } catch (BeanCurrentlyInCreationException ex) {
            System.out.println("DIRECT_CYCLE=" + ex.getMessage());
        }

        try {
            new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.failure.indirect");
        } catch (BeansException ex) {
            System.out.println("INDIRECT_CYCLE=" + ex.getMessage());
        }

        try {
            new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.failure.self");
        } catch (BeansException ex) {
            System.out.println("SELF_DEPENDENCY=" + ex.getMessage());
        }

        System.out.println("PHASE-1-FAILURE-PATH: PASS");
    }
}
