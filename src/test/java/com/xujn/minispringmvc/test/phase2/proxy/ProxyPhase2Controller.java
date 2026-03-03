package com.xujn.minispringmvc.test.phase2.proxy;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;

@Controller
public class ProxyPhase2Controller {

    @RequestMapping(path = "/phase2/proxy", method = "GET")
    public String proxy(@RequestParam("name") String name) {
        return name;
    }
}
