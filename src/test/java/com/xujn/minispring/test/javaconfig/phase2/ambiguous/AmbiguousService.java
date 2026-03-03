package com.xujn.minispring.test.javaconfig.phase2.ambiguous;

public class AmbiguousService {

    private final Client client;

    public AmbiguousService(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
