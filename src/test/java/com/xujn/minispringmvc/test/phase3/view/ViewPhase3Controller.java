package com.xujn.minispringmvc.test.phase3.view;

import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.servlet.ModelAndView;

@Controller
public class ViewPhase3Controller {

    @RequestMapping(path = "/phase3/view/string", method = "GET")
    public String stringView() {
        return "userDetail";
    }

    @RequestMapping(path = "/phase3/view/model", method = "GET")
    public ModelAndView modelView(@RequestParam("name") String name) {
        return new ModelAndView("userDetail").addObject("name", name);
    }
}
