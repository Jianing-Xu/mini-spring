package com.xujn.minispringmvc.examples.phase1.fixture;

import com.xujn.minispring.context.annotation.ComponentScan;

@ComponentScan({
        "com.xujn.minispringmvc.mapping",
        "com.xujn.minispringmvc.adapter",
        "com.xujn.minispringmvc.exception",
        "com.xujn.minispringmvc.examples.phase1.fixture"
})
public class Phase1ExampleConfig {
}
