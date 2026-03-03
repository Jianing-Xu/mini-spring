package com.xujn.minispring.examples.javaconfig.phase1.fixture.happy;

import com.xujn.minispring.context.annotation.Autowired;

public class AppService {

    @Autowired
    private UserRepository userRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
