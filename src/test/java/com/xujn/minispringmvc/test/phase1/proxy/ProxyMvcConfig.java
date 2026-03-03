package com.xujn.minispringmvc.test.phase1.proxy;

import com.xujn.minispring.context.annotation.ComponentScan;

@ComponentScan({
        "com.xujn.minispringmvc.mapping",
        "com.xujn.minispringmvc.adapter",
        "com.xujn.minispringmvc.exception",
        "com.xujn.minispringmvc.test.phase1.proxy"
})
public class ProxyMvcConfig {
}
