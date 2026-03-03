package com.xujn.minispringmvc.test.phase1.simple;

import com.xujn.minispring.context.annotation.ComponentScan;

@ComponentScan({
        "com.xujn.minispringmvc.mapping",
        "com.xujn.minispringmvc.adapter",
        "com.xujn.minispringmvc.exception",
        "com.xujn.minispringmvc.test.phase1.simple"
})
public class SimpleMvcConfig {
}
