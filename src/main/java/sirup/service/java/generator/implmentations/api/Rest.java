package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.common.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.Endpoint;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;
import static sirup.service.java.generator.implmentations.common.Endpoint.Method.*;

public final class Rest extends AbstractApi implements IApi {

    private final List<Endpoint> endpoints;

    public static final Rest DEFAULT;
    static {
        DEFAULT = new Rest();
        DEFAULT.addEndpoint(new Endpoint(GET, "/api/v1"));
    }

    private Rest() {
        this.endpoints = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "REST: {\n" +
                endpoints.stream().map(Endpoint::toString).collect(Collectors.joining("\n")) +
                "\n}";
    }

    private void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    static RestBuilder builder() {
        return new RestBuilder();
    }

    @Override
    public String getName() {
        return "Rest";
    }

    @Override
    public String getDependencyName() {
        return "apache spark";
    }

    private static final DataField request = new DataField(DataField.Type.REQUEST, "request");
    private static final DataField response = new DataField(DataField.Type.RESPONSE, "response");

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        fileWriter.write(packageString(this.getPackageName()));
        fileWriter.write(staticImportString("spark.Spark.*"));
        generateClass(fileWriter, this.getName(), () -> {
            generateMethod(fileWriter, "start", DataField.Type.VOID, null, () -> {
                for (Endpoint endpoint : this.endpoints) {
                    fileWriter.write("\t\t" + endpoint.method().method + "(\"" + endpoint.path() +
                            "\", ((request, response) -> \"todo\"));\n");
                }
            });
        });
    }

    public static class RestBuilder implements IApiBuilder<Rest> {
        private final Rest rest;
        private RestBuilder() {
            this.rest = new Rest();
        }

        public RestBuilder endpoint(Endpoint.Method method, String path) {
            return this.endpoint(new Endpoint(method, path));
        }
        public RestBuilder endpoint(Endpoint endpoint) {
            this.rest.addEndpoint(endpoint);
            return this;
        }

        @Override
        public Rest build() {
            return this.rest;
        }
    }
}
