package com.xujn.minispringmvc.examples.phase1.fixture;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.WebResponse;

@Controller
@RequestMapping(path = "/users")
public class Phase1ExampleController {

    @RequestMapping(path = "/detail", method = "GET")
    public String detail(
            @RequestParam("name") String name,
            @RequestParam("age") int age,
            @RequestParam("id") long id,
            @RequestParam("active") boolean active) {
        return name + "-" + age + "-" + id + "-" + active;
    }

    @RequestMapping(path = "/ping", method = "GET")
    public void ping(WebResponse response) {
        response.setStatus(204);
    }
}
