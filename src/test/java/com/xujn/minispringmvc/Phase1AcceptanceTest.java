package com.xujn.minispringmvc;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.exception.HandlerAdapterConflictException;
import com.xujn.minispringmvc.exception.MappingConflictException;
import com.xujn.minispringmvc.exception.NoHandlerAdapterException;
import com.xujn.minispringmvc.mapping.RequestMappingHandlerMapping;
import com.xujn.minispringmvc.servlet.DispatcherServlet;
import com.xujn.minispringmvc.servlet.SimpleWebRequest;
import com.xujn.minispringmvc.servlet.SimpleWebResponse;
import com.xujn.minispringmvc.test.phase1.conflict.ConflictMvcConfig;
import com.xujn.minispringmvc.test.phase1.conflict.ConflictControllerOne;
import com.xujn.minispringmvc.test.phase1.multiadapter.MultiAdapterMvcConfig;
import com.xujn.minispringmvc.test.phase1.multiadapter.MultiAdapterHandlerMapping;
import com.xujn.minispringmvc.test.phase1.noadapter.NoAdapterHandler;
import com.xujn.minispringmvc.test.phase1.noadapter.NoAdapterMvcConfig;
import com.xujn.minispringmvc.test.phase1.noadapter.NoAdapterHandlerMapping;
import com.xujn.minispringmvc.test.phase1.proxy.ProxyMvcConfig;
import com.xujn.minispringmvc.test.phase1.proxy.ProxiedControllerFixture;
import com.xujn.minispringmvc.test.phase1.simple.SimpleMvcConfig;
import com.xujn.minispringmvc.test.phase1.simple.SimpleControllerFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Phase1AcceptanceTest {

    @Test
    void shouldInitializeDispatcherAndBuildMappings() {
        AnnotationConfigApplicationContext context = mvcContext(SimpleMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        assertEquals(1, dispatcherServlet.getHandlerMappings().size());
        assertEquals(1, dispatcherServlet.getHandlerAdapters().size());
        assertEquals(1, dispatcherServlet.getExceptionResolvers().size());

        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        assertEquals(4, handlerMapping.getRegistrySize());
    }

    @Test
    void shouldDispatchExactGetPath() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/users/detail")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("id", "100")
                .addParameter("active", "true"));

        assertEquals(200, response.getStatus());
        assertEquals("alice-18-100-true", response.getBodyAsString());
    }

    @Test
    void shouldInjectWebRequestAndWebResponse() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebRequest request = SimpleWebRequest.get("/users/native");
        SimpleWebResponse response = dispatch(dispatcherServlet, request);

        assertEquals(200, response.getStatus());
        assertEquals("/users/native|GET", response.getBodyAsString());
    }

    @Test
    void shouldHandleVoidReturnValue() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/users/ping"));

        assertEquals(204, response.getStatus());
        assertEquals("", response.getBodyAsString());
    }

    @Test
    void shouldReturn404WhenNoHandlerFound() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/missing"));

        assertEquals(404, response.getStatus());
        assertTrue(response.getBodyAsString().contains("GET /missing"));
    }

    @Test
    void shouldReturn404WhenMethodDoesNotMatch() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.post("/users/detail"));

        assertEquals(404, response.getStatus());
        assertTrue(response.getBodyAsString().contains("POST /users/detail"));
    }

    @Test
    void shouldReturn400WhenRequestParamMissing() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/users/detail")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("active", "true"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBodyAsString().contains("id"));
    }

    @Test
    void shouldReturn400WhenTypeConversionFails() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/users/detail")
                .addParameter("name", "alice")
                .addParameter("age", "18")
                .addParameter("id", "abc")
                .addParameter("active", "true"));

        assertEquals(400, response.getStatus());
        assertTrue(response.getBodyAsString().contains("id"));
        assertTrue(response.getBodyAsString().contains("long"));
    }

    @Test
    void shouldReturn500WhenControllerThrowsException() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mvcContext(SimpleMvcConfig.class));
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/users/error"));

        assertEquals(500, response.getStatus());
        assertTrue(response.getBodyAsString().contains("SimpleControllerFixture#error"));
        assertTrue(response.getBodyAsString().contains("boom"));
    }

    @Test
    void shouldFailFastWhenMappingsConflict() {
        AnnotationConfigApplicationContext context = mvcContext(ConflictMvcConfig.class);

        assertThrows(MappingConflictException.class, () -> new DispatcherServlet(context));
    }

    @Test
    void shouldFailFastWhenNoHandlerAdapterAvailable() {
        AnnotationConfigApplicationContext context = mvcContext(NoAdapterMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        NoHandlerAdapterException exception = assertThrows(NoHandlerAdapterException.class,
                () -> dispatch(dispatcherServlet, SimpleWebRequest.get("/no-adapter")));

        assertTrue(exception.getMessage().contains(NoAdapterHandler.class.getName()));
    }

    @Test
    void shouldFailFastWhenMultipleHandlerAdaptersAvailable() {
        AnnotationConfigApplicationContext context = mvcContext(MultiAdapterMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        HandlerAdapterConflictException exception = assertThrows(HandlerAdapterConflictException.class,
                () -> dispatch(dispatcherServlet, SimpleWebRequest.get("/multi-adapter")));

        assertTrue(exception.getMessage().contains("count [2]"));
    }

    @Test
    void shouldResolveControllerMetadataWhenControllerIsProxied() {
        AnnotationConfigApplicationContext context = mvcContext(ProxyMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        SimpleWebResponse response = dispatch(dispatcherServlet, SimpleWebRequest.get("/proxy/hello").addParameter("name", "bob"));

        assertEquals(200, response.getStatus());
        assertEquals("proxy-bob", response.getBodyAsString());
    }

    @Test
    void shouldUseContainerManagedMvcBeans() {
        AnnotationConfigApplicationContext context = mvcContext(SimpleMvcConfig.class);
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        assertNotNull(context.getBean(RequestMappingHandlerMapping.class));
        assertNotNull(context.getBean(com.xujn.minispringmvc.adapter.RequestMappingHandlerAdapter.class));
        assertNotNull(context.getBean(com.xujn.minispringmvc.exception.DefaultHandlerExceptionResolver.class));
        assertSame(context.getBean(RequestMappingHandlerMapping.class), dispatcherServlet.getHandlerMappings().get(0));
    }

    private AnnotationConfigApplicationContext mvcContext(Class<?> configClass) {
        return new AnnotationConfigApplicationContext(configClass);
    }

    private SimpleWebResponse dispatch(DispatcherServlet dispatcherServlet, SimpleWebRequest request) {
        SimpleWebResponse response = new SimpleWebResponse();
        dispatcherServlet.service(request, response);
        return response;
    }
}
