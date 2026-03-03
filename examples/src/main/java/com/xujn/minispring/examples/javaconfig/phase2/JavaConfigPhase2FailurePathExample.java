package com.xujn.minispring.examples.javaconfig.phase2;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeanDefinitionOverrideException;
import com.xujn.minispring.exception.BeansException;

/**
 * Manual verification entry for JavaConfig Phase 2 failure-path behavior.
 */
public class JavaConfigPhase2FailurePathExample {

    public static void main(String[] args) {
        BeansException missing = capture("com.xujn.minispring.examples.javaconfig.phase2.fixture.failure.missing");
        BeanDefinitionOverrideException conflict = (BeanDefinitionOverrideException) capture(
                "com.xujn.minispring.examples.javaconfig.phase2.fixture.failure.conflict");

        System.out.println("MISSING_PARAMETER_ERROR=" + missing.getMessage());
        System.out.println("CONFLICT_ERROR=" + conflict.getMessage());
        System.out.println("PHASE-JAVACONFIG-2-FAILURE-PATH: PASS");
    }

    private static BeansException capture(String basePackage) {
        try {
            new AnnotationConfigApplicationContext(basePackage);
            throw new IllegalStateException("Expected JavaConfig failure for package " + basePackage);
        } catch (BeansException ex) {
            return ex;
        }
    }
}
