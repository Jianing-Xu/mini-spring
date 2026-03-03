package com.xujn.minispring.test.phase1.interfaceinject;

import com.xujn.minispring.context.annotation.Component;

@Component
public class JpaUserRepository implements UserRepository {

    @Override
    public String kind() {
        return "jpa";
    }
}
