package com.xujn.minispringmvc;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.servlet.DispatcherServlet;
import com.xujn.minispringmvc.servlet.SimpleWebRequest;
import com.xujn.minispringmvc.servlet.SimpleWebResponse;
import com.xujn.minispringmvc.test.phase3.exception.ExceptionPhase3MvcConfig;
import com.xujn.minispringmvc.test.phase3.exception.ExceptionTraceRecorder;
import com.xujn.minispringmvc.test.phase3.interceptor.InterceptorPhase3MvcConfig;
import com.xujn.minispringmvc.test.phase3.interceptor.InterceptorTraceRecorder;
import com.xujn.minispringmvc.test.phase3.shortcircuit.ShortCircuitMvcConfig;
import com.xujn.minispringmvc.test.phase3.shortcircuit.ShortCircuitTraceRecorder;
import com.xujn.minispringmvc.test.phase3.view.ViewPhase3MvcConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase3AcceptanceTest {

    @Test
    void shouldExecuteInterceptorsInOrderAndReverseCallbacks() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(InterceptorPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        InterceptorTraceRecorder recorder = context.getBean(InterceptorTraceRecorder.class);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/interceptor"));

        assertEquals(200, response.getStatus());
        assertEquals("ok", response.getBodyAsString());
        assertEquals(
                "pre:first,pre:second,post:second,post:first,after:second,after:first",
                String.join(",", recorder.getEvents())
        );
    }

    @Test
    void shouldShortCircuitInterceptorAndOnlyCompleteAppliedOnes() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ShortCircuitMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ShortCircuitTraceRecorder recorder = context.getBean(ShortCircuitTraceRecorder.class);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/short"));

        assertEquals(403, response.getStatus());
        assertEquals("blocked", response.getBodyAsString());
        assertEquals(
                "pre:first,pre:short,after:first",
                String.join(",", recorder.getEvents())
        );
    }

    @Test
    void shouldResolveExceptionWithHighPriorityResolver() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ExceptionPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ExceptionTraceRecorder recorder = context.getBean(ExceptionTraceRecorder.class);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/exception/custom"));

        assertEquals(418, response.getStatus());
        assertEquals("custom-error", response.getBodyAsString());
        assertEquals("customResolver", recorder.getEvents().get(0));
    }

    @Test
    void shouldFallbackToDefaultExceptionResolver() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ExceptionPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/exception/default"));

        assertEquals(500, response.getStatus());
        assertTrue(response.getBodyAsString().contains("default-boom"));
    }

    @Test
    void shouldNotRewriteCommittedResponseWhenExceptionOccursAfterCommit() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ExceptionPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ExceptionTraceRecorder recorder = context.getBean(ExceptionTraceRecorder.class);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/exception/committed"));

        assertEquals(200, response.getStatus());
        assertEquals("direct", response.getBodyAsString());
        assertTrue(recorder.getEvents().contains("afterCompletion"));
    }

    @Test
    void shouldRenderStringViewNameWhenViewResolverPresent() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ViewPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/view/string").addParameter("name", "alice"));

        assertEquals(200, response.getStatus());
        assertEquals("view:userDetail|name=alice", response.getBodyAsString());
    }

    @Test
    void shouldRenderModelAndViewWhenViewResolverPresent() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ViewPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/view/model").addParameter("name", "alice"));

        assertEquals(200, response.getStatus());
        assertEquals("view:userDetail|name=alice", response.getBodyAsString());
    }

    @Test
    void shouldKeepStringAsBodyWhenViewResolverDisabled() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(InterceptorPhase3MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase3/plain"));

        assertEquals(200, response.getStatus());
        assertEquals("plain-text", response.getBodyAsString());
    }

    private SimpleWebResponse dispatch(DispatcherServlet dispatcherServlet, SimpleWebRequest request) {
        SimpleWebResponse response = new SimpleWebResponse();
        dispatcherServlet.service(request, response);
        return response;
    }
}
