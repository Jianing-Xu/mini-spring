package com.xujn.minispringmvc.test.phase1.proxy;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;

@Controller
@RequestMapping(path = "/proxy")
public class ProxiedControllerFixture implements ProxiedControllerContract {

    @Override
    @RequestMapping(path = "/hello", method = "GET")
    public String hello(@RequestParam("name") String name) {
        return "proxy-" + name;
    }
}
