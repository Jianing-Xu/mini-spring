package com.xujn.minispringmvc.examples.phase2;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.examples.phase2.fixture.Phase2ExampleConfig;
import com.xujn.minispringmvc.servlet.DispatcherServlet;
import com.xujn.minispringmvc.servlet.SimpleWebRequest;
import com.xujn.minispringmvc.servlet.SimpleWebResponse;

/**
 * Runnable Phase 2 MVC example for manual acceptance.
 */
public class Phase2BindingExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Phase2ExampleConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse defaultResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/phase2/example/default")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("id", "100")
                .addParameter("active", "true"), defaultResponse);

        SimpleWebResponse overrideResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/phase2/example/override")
                .addParameter("name", "alice"), overrideResponse);

        SimpleWebResponse committedResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/phase2/example/committed"), committedResponse);

        System.out.println("DEFAULT_BODY=" + defaultResponse.getBodyAsString());
        System.out.println("OVERRIDE_BODY=" + overrideResponse.getBodyAsString());
        System.out.println("COMMITTED_BODY=" + committedResponse.getBodyAsString());
        boolean pass = "alice-18-100-true".equals(defaultResponse.getBodyAsString())
                && "example-handled-custom-alice".equals(overrideResponse.getBodyAsString())
                && "direct".equals(committedResponse.getBodyAsString());
        System.out.println("PHASE-MVC-2-HAPPY-PATH: " + (pass ? "PASS" : "FAIL"));
    }
}
