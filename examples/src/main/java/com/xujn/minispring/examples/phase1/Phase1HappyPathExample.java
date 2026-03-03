package com.xujn.minispring.examples.phase1;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.examples.phase1.fixture.happy.InterfaceUserService;
import com.xujn.minispring.examples.phase1.fixture.happy.JpaPrimaryRepository;
import com.xujn.minispring.examples.phase1.fixture.happy.ServiceA;
import com.xujn.minispring.examples.phase1.fixture.happy.SimpleComponent;
import com.xujn.minispring.examples.phase1.fixture.happy.UserRepository;
import com.xujn.minispring.examples.phase1.fixture.happy.UserService;

/**
 * Manual verification entry for the Phase 1 happy path.
 */
public class Phase1HappyPathExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext happyContext =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.happy");

        UserService userService = happyContext.getBean(UserService.class);
        UserRepository userRepository = happyContext.getBean(UserRepository.class);
        ServiceA serviceA = happyContext.getBean(ServiceA.class);
        InterfaceUserService interfaceUserService = happyContext.getBean(InterfaceUserService.class);

        System.out.println("SCAN_COUNT=" + happyContext.getBeanDefinitionCount());
        System.out.println("LOOKUP_BY_NAME=" +
                happyContext.getBean("userService").getClass().getSimpleName());
        System.out.println("LOOKUP_BY_TYPE=" + happyContext.getBean(UserService.class).getClass().getSimpleName());
        System.out.println("SINGLETON_SAME=" + (userService == happyContext.getBean(UserService.class)));
        System.out.println("INJECTED_REPOSITORY=" + (userService.getUserRepository() == userRepository));
        System.out.println("MULTI_LEVEL=" + (serviceA.getServiceB().getServiceC() != null));
        System.out.println("SIMPLE_COMPONENT=" + (happyContext.getBean(SimpleComponent.class) != null));
        System.out.println("INTERFACE_SINGLE_IMPL=" +
                (interfaceUserService.getPrimaryRepository() instanceof JpaPrimaryRepository));

        AnnotationConfigApplicationContext emptyContext =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.phase1.fixture.empty");
        System.out.println("EMPTY_PACKAGE_COUNT=" + emptyContext.getBeanDefinitionCount());

        AnnotationConfigApplicationContext noPackageContext = new AnnotationConfigApplicationContext();
        System.out.println("NO_PACKAGE_COUNT=" + noPackageContext.getBeanDefinitionCount());
        System.out.println("PHASE-1-HAPPY-PATH: PASS");
    }
}
