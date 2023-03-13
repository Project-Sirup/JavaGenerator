package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.IApi;

public final class Grpc extends AbstractApi implements IApi {
    @Override
    public String getName() {
        return "gRPC";
    }

    @Override
    public String getDependencyName() {
        return "gRPC";
    }

    @Override
    public void generate() {

    }
}
