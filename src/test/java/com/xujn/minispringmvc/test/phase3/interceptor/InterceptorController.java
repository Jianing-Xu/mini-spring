package com.xujn.minispringmvc.test.phase3.interceptor;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

@Controller
public class InterceptorController {

    @RequestMapping(path = "/phase3/interceptor", method = "GET")
    public String intercept() {
        return "ok";
    }

    @RequestMapping(path = "/phase3/plain", method = "GET")
    public String plain() {
        return "plain-text";
    }
}
