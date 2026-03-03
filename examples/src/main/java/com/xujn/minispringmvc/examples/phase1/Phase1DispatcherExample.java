package com.xujn.minispringmvc.examples.phase1;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.examples.phase1.fixture.Phase1ExampleConfig;
import com.xujn.minispringmvc.examples.phase1.fixture.Phase1ExampleController;
import com.xujn.minispringmvc.servlet.DispatcherServlet;
import com.xujn.minispringmvc.servlet.SimpleWebRequest;
import com.xujn.minispringmvc.servlet.SimpleWebResponse;

/**
 * Runnable Phase 1 MVC example for manual acceptance.
 */
public class Phase1DispatcherExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                Phase1ExampleConfig.class
        );
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse detailResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/users/detail")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("id", "100")
                .addParameter("active", "true"), detailResponse);

        SimpleWebResponse pingResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/users/ping"), pingResponse);

        SimpleWebResponse missingResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/missing"), missingResponse);

        System.out.println("DETAIL_STATUS=" + detailResponse.getStatus());
        System.out.println("DETAIL_BODY=" + detailResponse.getBodyAsString());
        System.out.println("PING_STATUS=" + pingResponse.getStatus());
        System.out.println("PING_BODY_LENGTH=" + pingResponse.getBodyAsString().length());
        System.out.println("MISSING_STATUS=" + missingResponse.getStatus());
        System.out.println("MISSING_BODY=" + missingResponse.getBodyAsString());

        boolean pass = detailResponse.getStatus() == 200
                && "alice-18-100-true".equals(detailResponse.getBodyAsString())
                && pingResponse.getStatus() == 204
                && missingResponse.getStatus() == 404;
        System.out.println("PHASE-MVC-1-HAPPY-PATH: " + (pass ? "PASS" : "FAIL"));
    }
}
