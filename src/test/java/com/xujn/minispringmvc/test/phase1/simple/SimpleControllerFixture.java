package com.xujn.minispringmvc.test.phase1.simple;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

@Controller
@RequestMapping(path = "/users")
public class SimpleControllerFixture {

    @RequestMapping(path = "/detail", method = "GET")
    public String detail(
            @RequestParam("name") String name,
            @RequestParam("age") int age,
            @RequestParam("id") long id,
            @RequestParam("active") boolean active) {
        return name + "-" + age + "-" + id + "-" + active;
    }

    @RequestMapping(path = "/native", method = "GET")
    public String nativeRequest(WebRequest request, WebResponse response) {
        return request.getRequestUri() + "|" + request.getMethod();
    }

    @RequestMapping(path = "/ping", method = "GET")
    public void ping(WebResponse response) {
        response.setStatus(204);
    }

    @RequestMapping(path = "/error", method = "GET")
    public String error() {
        throw new IllegalStateException("boom");
    }
}
