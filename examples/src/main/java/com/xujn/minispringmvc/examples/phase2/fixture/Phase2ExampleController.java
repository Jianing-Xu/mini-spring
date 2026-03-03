package com.xujn.minispringmvc.examples.phase2.fixture;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.WebResponse;

@Controller
public class Phase2ExampleController {

    @RequestMapping(path = "/phase2/example/default", method = "GET")
    public String defaultBinding(
            @RequestParam("name") String name,
            @RequestParam("age") int age,
            @RequestParam("id") long id,
            @RequestParam("active") boolean active) {
        return name + "-" + age + "-" + id + "-" + active;
    }

    @RequestMapping(path = "/phase2/example/override", method = "GET")
    public String override(@RequestParam("name") String name) {
        return name;
    }

    @RequestMapping(path = "/phase2/example/committed", method = "GET")
    public String committed(WebResponse response) {
        response.write("direct");
        return "ignored";
    }
}
