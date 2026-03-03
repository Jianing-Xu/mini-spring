package com.xujn.minispring.examples.phase1.fixture.happy;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class InterfaceUserService {

    @Autowired
    private PrimaryRepository primaryRepository;

    public PrimaryRepository getPrimaryRepository() {
        return primaryRepository;
    }
}
