package com.xujn.minispringmvc.test.phase2.defaultcase;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

@Controller
public class DefaultPhase2Controller {

    @RequestMapping(path = "/phase2/default", method = "GET")
    public String defaultBinding(
            @RequestParam("name") String name,
            @RequestParam("age") int age,
            @RequestParam("id") long id,
            @RequestParam("active") boolean active,
            WebRequest request,
            WebResponse response) {
        response.setHeader("X-Request-Uri", request.getRequestUri());
        return name + "-" + age + "-" + id + "-" + active;
    }

    @RequestMapping(path = "/phase2/string", method = "GET")
    public String stringValue() {
        return "ok";
    }

    @RequestMapping(path = "/phase2/void", method = "GET")
    public void voidValue(WebResponse response) {
        response.setStatus(204);
    }

    @RequestMapping(path = "/phase2/committed", method = "GET")
    public String committed(WebResponse response) {
        response.write("direct");
        return "ignored";
    }
}
