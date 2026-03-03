package com.xujn.minispringmvc.test.phase3.shortcircuit;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

@Controller
public class ShortCircuitController {

    @RequestMapping(path = "/phase3/short", method = "GET")
    public String shouldNotRun() {
        return "should-not-run";
    }
}
