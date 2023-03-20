package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.common.Endpoint;
import sirup.service.java.generator.implmentations.common.EndpointGroup;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class Grpc extends AbstractApi {

    private Grpc() {}

    @Override
    public String getName() {
        return "Grpc";
    }

    @Override
    public String getDependencyName() {
        return "gRPC";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classBody(classGenerator -> {
                    classGenerator.write("\t\t//TODO: implement gRPC\n");
                })
                .build()
                .make();
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
        public IApiBuilder<Grpc> port(int port) {
            return this;
        }

        @Override
        public IApiBuilder<Grpc> endpoint(Endpoint.HttpMethod httpMethod, String path, String linkedMethodName) {
            return this;
        }

        @Override
        public IApiBuilder<Grpc> endpoints(List<Endpoint> endpoints) {
            return this;
        }

        @Override
        public IApiBuilder<Grpc> endpoint(Endpoint endpoint) {
            return this;
        }

        @Override
        public IApiBuilder<Grpc> endpointGroup(EndpointGroup endpointGroup) {
            return this;
        }

        @Override
        public IApiBuilder<Grpc> endpointGroup(EndpointGroup.EndpointGroupBuilder endpointGroupBuilder) {
            return this;
        }

        @Override
        public Grpc build() {
            return this.grpc;
        }
    }
}
