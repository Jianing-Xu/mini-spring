package com.xujn.minispringmvc.test.phase2.override;

import com.xujn.minispring.context.annotation.ComponentScan;

@ComponentScan({
        "com.xujn.minispringmvc.mapping",
        "com.xujn.minispringmvc.adapter",
        "com.xujn.minispringmvc.bind",
        "com.xujn.minispringmvc.exception",
        "com.xujn.minispringmvc.test.phase2.override"
})
public class OverridePhase2MvcConfig {
}
