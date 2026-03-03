package com.xujn.minispringmvc.examples.phase3;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.examples.phase3.fixture.Phase3ExampleConfig;
import com.xujn.minispringmvc.servlet.DispatcherServlet;
import com.xujn.minispringmvc.servlet.SimpleWebRequest;
import com.xujn.minispringmvc.servlet.SimpleWebResponse;

/**
 * Runnable Phase 3 MVC example for manual acceptance.
 */
public class Phase3MvcExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Phase3ExampleConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse interceptorResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/phase3/example/interceptor"), interceptorResponse);

        SimpleWebResponse viewResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/phase3/example/view").addParameter("name", "alice"), viewResponse);

        SimpleWebResponse shortResponse = new SimpleWebResponse();
        dispatcherServlet.service(SimpleWebRequest.get("/phase3/example/short"), shortResponse);

        System.out.println("INTERCEPTOR_BODY=" + interceptorResponse.getBodyAsString());
        System.out.println("VIEW_BODY=" + viewResponse.getBodyAsString());
        System.out.println("SHORT_STATUS=" + shortResponse.getStatus());
        System.out.println("SHORT_BODY=" + shortResponse.getBodyAsString());
        boolean pass = "view:ok".equals(interceptorResponse.getBodyAsString())
                && "view:userDetail|name=alice".equals(viewResponse.getBodyAsString())
                && shortResponse.getStatus() == 403
                && "blocked".equals(shortResponse.getBodyAsString());
        System.out.println("PHASE-MVC-3-HAPPY-PATH: " + (pass ? "PASS" : "FAIL"));
    }
}
