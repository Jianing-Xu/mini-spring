package com.xujn.minispringmvc.examples.phase2.fixture;

import com.xujn.minispring.context.annotation.ComponentScan;

@ComponentScan({
        "com.xujn.minispringmvc.mapping",
        "com.xujn.minispringmvc.adapter",
        "com.xujn.minispringmvc.bind",
        "com.xujn.minispringmvc.exception",
        "com.xujn.minispringmvc.examples.phase2.fixture"
})
public class Phase2ExampleConfig {
}
