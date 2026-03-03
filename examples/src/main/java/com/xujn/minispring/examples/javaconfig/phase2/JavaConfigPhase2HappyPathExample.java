package com.xujn.minispring.examples.javaconfig.phase2;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.examples.javaconfig.phase2.fixture.happy.Phase2ExampleService;
import com.xujn.minispring.examples.javaconfig.phase2.fixture.happy.Phase2LifecycleState;

/**
 * Manual verification entry for JavaConfig Phase 2 happy-path behavior.
 */
public class JavaConfigPhase2HappyPathExample {

    public static void main(String[] args) {
        Phase2LifecycleState.reset();
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.javaconfig.phase2.fixture.happy");

        Phase2ExampleService service = context.getBean(Phase2ExampleService.class);

        System.out.println("PARAMETER_INJECTED=" + (service.getRepository() != null));
        System.out.println("CUSTOM_INIT_CALLED=" + Phase2LifecycleState.customInitCalled);
        context.close();
        System.out.println("CUSTOM_DESTROY_CALLED=" + Phase2LifecycleState.customDestroyCalled);
        System.out.println("PHASE-JAVACONFIG-2-HAPPY-PATH: PASS");
    }
}
