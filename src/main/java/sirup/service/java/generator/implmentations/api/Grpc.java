package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;

public final class Grpc extends AbstractApi implements IApi {

    private Grpc() {}

    @Override
    public String getName() {
        return "gRPC";
    }

    @Override
    public String getDependencyName() {
        return "gRPC";
    }

    @Override
    public void fillFile(FileWriter writer) {

    }

    static GrpcBuilder builder() {
        return new GrpcBuilder();
    }

    public static class GrpcBuilder implements IApiBuilder<Grpc> {
        private final Grpc grpc;
        private GrpcBuilder() {
            this.grpc = new Grpc();
        }

        @Override
        public Grpc build() {
            return this.grpc;
        }
    }
}
