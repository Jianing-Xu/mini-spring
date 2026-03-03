package com.xujn.minispringmvc.test.phase3.shortcircuit;

import com.xujn.minispring.context.annotation.ComponentScan;

@ComponentScan({
        "com.xujn.minispringmvc.mapping",
        "com.xujn.minispringmvc.adapter",
        "com.xujn.minispringmvc.bind",
        "com.xujn.minispringmvc.exception",
        "com.xujn.minispringmvc.test.phase3.shortcircuit"
})
public class ShortCircuitMvcConfig {
}
