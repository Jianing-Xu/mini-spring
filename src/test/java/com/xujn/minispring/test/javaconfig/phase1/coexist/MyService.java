package com.xujn.minispring.test.javaconfig.phase1.coexist;

import com.xujn.minispring.context.annotation.Autowired;

public class MyService {

    @Autowired
    private UserRepository userRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
