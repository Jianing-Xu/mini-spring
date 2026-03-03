package com.xujn.minispringmvc.test.phase2.override;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;

@Controller
public class OverridePhase2Controller {

    @RequestMapping(path = "/phase2/override-param", method = "GET")
    public String overrideParam(@RequestParam("name") String name) {
        return name;
    }

    @RequestMapping(path = "/phase2/override-return", method = "GET")
    public String overrideReturn() {
        return "ok";
    }
}
