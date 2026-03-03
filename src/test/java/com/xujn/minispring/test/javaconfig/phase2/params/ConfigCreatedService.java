package com.xujn.minispring.test.javaconfig.phase2.params;

public class ConfigCreatedService {

    private final Repository repository;

    public ConfigCreatedService(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public boolean isReady() {
        return repository != null;
    }
}
