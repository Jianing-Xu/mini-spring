package com.xujn.minispringmvc.examples.phase3.fixture;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

@Controller
public class Phase3ExampleController {

    @RequestMapping(path = "/phase3/example/interceptor", method = "GET")
    public String interceptor() {
        return "okPage";
    }

    @RequestMapping(path = "/phase3/example/view", method = "GET")
    public String view() {
        return "userDetail";
    }

    @RequestMapping(path = "/phase3/example/short", method = "GET")
    public String shortPath() {
        return "should-not-run";
    }
}
