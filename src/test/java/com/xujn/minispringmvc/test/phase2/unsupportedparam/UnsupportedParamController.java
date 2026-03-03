package com.xujn.minispringmvc.test.phase2.unsupportedparam;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
public class UnsupportedParamController {

    @RequestMapping(path = "/phase2/unsupported-param", method = "GET")
    public String unsupported(LocalDate date) {
        return String.valueOf(date);
    }
}
