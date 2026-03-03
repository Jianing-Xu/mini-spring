package com.xujn.minispring.test.phase1.interfaceinject;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;

@Component
public class UserService {

    @Autowired
    private UserRepository repo;

    public UserRepository getRepo() {
        return repo;
    }
}
