package com.xujn.minispringmvc.test.phase3.exception;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.servlet.WebResponse;

@Controller
public class ExceptionController {

    @RequestMapping(path = "/phase3/exception/custom", method = "GET")
    public String custom() {
        throw new IllegalArgumentException("custom-boom");
    }

    @RequestMapping(path = "/phase3/exception/default", method = "GET")
    public String fallback() {
        throw new IllegalStateException("default-boom");
    }

    @RequestMapping(path = "/phase3/exception/committed", method = "GET")
    public String committed(WebResponse response) {
        response.write("direct");
        throw new IllegalStateException("after-commit");
    }
}
