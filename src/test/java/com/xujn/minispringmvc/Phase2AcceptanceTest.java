package com.xujn.minispringmvc;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.adapter.RequestMappingHandlerAdapter;
import com.xujn.minispringmvc.exception.UnsupportedHandlerMethodParameterException;
import com.xujn.minispringmvc.exception.UnsupportedHandlerMethodReturnValueException;
import com.xujn.minispringmvc.servlet.DispatcherServlet;
import com.xujn.minispringmvc.servlet.SimpleWebRequest;
import com.xujn.minispringmvc.servlet.SimpleWebResponse;
import com.xujn.minispringmvc.test.phase2.defaultcase.DefaultPhase2MvcConfig;
import com.xujn.minispringmvc.test.phase2.override.OverridePhase2MvcConfig;
import com.xujn.minispringmvc.test.phase2.proxy.ProxyPhase2MvcConfig;
import com.xujn.minispringmvc.test.phase2.unsupportedparam.UnsupportedParamMvcConfig;
import com.xujn.minispringmvc.test.phase2.unsupportedreturn.UnsupportedReturnMvcConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase2AcceptanceTest {

    @Test
    void shouldCollectAndSortArgumentResolvers() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DefaultPhase2MvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);

        assertEquals(3, adapter.getArgumentResolvers().getResolvers().size());
        assertEquals("WebRequestArgumentResolver", adapter.getArgumentResolvers().getResolvers().get(0).getClass().getSimpleName());
        assertEquals("WebResponseArgumentResolver", adapter.getArgumentResolvers().getResolvers().get(1).getClass().getSimpleName());
        assertEquals("RequestParamArgumentResolver", adapter.getArgumentResolvers().getResolvers().get(2).getClass().getSimpleName());
        assertEquals(1, dispatcherServlet.getHandlerAdapters().size());
    }

    @Test
    void shouldCollectAndSortReturnValueHandlers() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DefaultPhase2MvcConfig.class);
        new DispatcherServlet(context);
        RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);

        assertEquals(2, adapter.getReturnValueHandlers().getHandlers().size());
        assertEquals("VoidReturnValueHandler", adapter.getReturnValueHandlers().getHandlers().get(0).getClass().getSimpleName());
        assertEquals("StringReturnValueHandler", adapter.getReturnValueHandlers().getHandlers().get(1).getClass().getSimpleName());
    }

    @Test
    void shouldResolveDefaultRequestParamsAndNativeObjects() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(DefaultPhase2MvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/default")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("id", "100")
                .addParameter("active", "true"));

        assertEquals(200, response.getStatus());
        assertEquals("alice-18-100-true", response.getBodyAsString());
    }

    @Test
    void shouldHandleStringAndVoidReturnValues() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(DefaultPhase2MvcConfig.class));

        SimpleWebResponse stringResponse = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/string"));
        assertEquals("ok", stringResponse.getBodyAsString());

        SimpleWebResponse voidResponse = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/void"));
        assertEquals(204, voidResponse.getStatus());
        assertEquals("", voidResponse.getBodyAsString());
    }

    @Test
    void shouldAllowCustomHighPriorityArgumentResolverToOverrideDefaultResolver() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(OverridePhase2MvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/override-param")
                .addParameter("name", "alice"));

        assertEquals("custom-alice", response.getBodyAsString());
    }

    @Test
    void shouldAllowCustomHighPriorityReturnValueHandlerToOverrideDefaultHandler() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(OverridePhase2MvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/override-return"));

        assertEquals("handled-ok", response.getBodyAsString());
    }

    @Test
    void shouldFailFastWhenNoArgumentResolverSupportsParameter() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(UnsupportedParamMvcConfig.class));

        UnsupportedHandlerMethodParameterException exception = assertThrows(
                UnsupportedHandlerMethodParameterException.class,
                () -> dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/unsupported-param"))
        );

        assertTrue(exception.getMessage().contains("parameter index [0]"));
        assertTrue(exception.getMessage().contains("java.time.LocalDate"));
    }

    @Test
    void shouldReturn400WhenDefaultRequestParamConversionFails() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(DefaultPhase2MvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/default")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("id", "bad")
                .addParameter("active", "true"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBodyAsString().contains("id"));
        assertTrue(response.getBodyAsString().contains("long"));
    }

    @Test
    void shouldFailFastWhenNoReturnValueHandlerSupportsReturnType() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(UnsupportedReturnMvcConfig.class));

        UnsupportedHandlerMethodReturnValueException exception = assertThrows(
                UnsupportedHandlerMethodReturnValueException.class,
                () -> dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/unsupported-return"))
        );

        assertTrue(exception.getMessage().contains("java.lang.Integer"));
    }

    @Test
    void shouldNotRewriteResponseWhenAlreadyCommitted() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(DefaultPhase2MvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/committed"));

        assertEquals(200, response.getStatus());
        assertEquals("direct", response.getBodyAsString());
    }

    @Test
    void shouldSupportProxiedCustomResolverAndHandlerBeans() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(new AnnotationConfigApplicationContext(ProxyPhase2MvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/phase2/proxy")
                .addParameter("name", "alice"));

        assertEquals("proxy-handled-proxy-alice", response.getBodyAsString());
    }

    private SimpleWebResponse dispatch(DispatcherServlet dispatcherServlet, SimpleWebRequest request) {
        SimpleWebResponse response = new SimpleWebResponse();
        dispatcherServlet.service(request, response);
        return response;
    }
}
