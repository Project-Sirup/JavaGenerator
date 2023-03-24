package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;
import java.io.IOException;

import static sirup.service.java.generator.implmentations.common.StringUtil.tab;

public final class Grpc extends AbstractApi {

    private Grpc() {}

    @Override
    public String getName() {
        return "Grpc";
    }

    @Override
    public String getDependencyName() {
        return "rpc";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classBody(classGenerator -> {
                    classGenerator.write(tab(1) + "//TODO: implement gRPC\n");
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
        public IApiBuilder<Grpc> options(MicroserviceRequest.Microservice.Api.Options options) {

            return this;
        }

        @Override
        public Grpc build() {
            return this.grpc;
        }
    }
}
