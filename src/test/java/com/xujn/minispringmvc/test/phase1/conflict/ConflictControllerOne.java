package com.xujn.minispringmvc.test.phase1.conflict;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

@Controller
public class ConflictControllerOne {

    @RequestMapping(path = "/conflict", method = "GET")
    public String one() {
        return "one";
    }
}
