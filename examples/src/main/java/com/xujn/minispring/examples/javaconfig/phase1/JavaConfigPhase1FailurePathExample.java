package com.xujn.minispring.examples.javaconfig.phase1;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.exception.BeansException;

/**
 * Manual verification entry for JavaConfig Phase 1 failure-path behavior.
 */
public class JavaConfigPhase1FailurePathExample {

    public static void main(String[] args) {
        BeansException nullException = capture("com.xujn.minispring.examples.javaconfig.phase1.fixture.failure.nullcase");
        BeansException throwingException = capture(
                "com.xujn.minispring.examples.javaconfig.phase1.fixture.failure.throwing");

        System.out.println("NULL_METHOD_ERROR=" + nullException.getMessage());
        System.out.println("THROWING_METHOD_ERROR=" + throwingException.getMessage());
        System.out.println("PHASE-JAVACONFIG-1-FAILURE-PATH: PASS");
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
