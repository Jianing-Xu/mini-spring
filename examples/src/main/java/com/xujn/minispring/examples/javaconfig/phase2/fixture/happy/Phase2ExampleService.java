package com.xujn.minispring.examples.javaconfig.phase2.fixture.happy;

public class Phase2ExampleService {

    private final Phase2Repository repository;

    public Phase2ExampleService(Phase2Repository repository) {
        this.repository = repository;
    }

    public void init() {
        Phase2LifecycleState.customInitCalled = true;
    }

    public void cleanup() {
        Phase2LifecycleState.customDestroyCalled = true;
    }

    public Phase2Repository getRepository() {
        return repository;
    }
}
