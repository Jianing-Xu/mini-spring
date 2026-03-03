package com.xujn.minispringmvc.test.phase1.conflict;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

@Controller
public class ConflictControllerTwo {

    @RequestMapping(path = "/conflict", method = "GET")
    public String two() {
        return "two";
    }
}
