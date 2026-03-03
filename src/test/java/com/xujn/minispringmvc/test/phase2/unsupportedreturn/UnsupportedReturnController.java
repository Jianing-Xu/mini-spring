package com.xujn.minispringmvc.test.phase2.unsupportedreturn;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

@Controller
public class UnsupportedReturnController {

    @RequestMapping(path = "/phase2/unsupported-return", method = "GET")
    public Integer unsupportedReturn() {
        return 1;
    }
}
