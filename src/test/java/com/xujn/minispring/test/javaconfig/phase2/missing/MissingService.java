package com.xujn.minispring.test.javaconfig.phase2.missing;

public class MissingService {

    private final MissingDependency dependency;

    public MissingService(MissingDependency dependency) {
        this.dependency = dependency;
    }

    public MissingDependency getDependency() {
        return dependency;
    }
}
