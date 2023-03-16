package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.common.*;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;
import static sirup.service.java.generator.implmentations.common.Endpoint.HttpMethod.*;
import static sirup.service.java.generator.implmentations.common.DataField.Type.*;

public final class Rest extends AbstractApi {

    private final List<Endpoint> endpoints;
    private final List<EndpointGroup> endpointGroups;

    public static final Rest DEFAULT;
    static {
        DEFAULT = new Rest();
        EndpointGroup baseGroup = EndpointGroup
                .builder()
                .groupName("/api/v1")
                .endpoint(new Endpoint(GET, "", null))
                .build();
        DEFAULT.addEndpointGroup(baseGroup);
    }

    private Rest() {
        this.endpoints = new ArrayList<>();
        this.endpointGroups = new ArrayList<>();
        this.controllers = new ArrayList<>();
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

    private void addEndpointGroup(EndpointGroup endpointGroup) {
        this.endpointGroups.add(endpointGroup);
        updateControllers(endpointGroup);
    }
    private void updateControllers(EndpointGroup endpointGroup) {
        if (endpointGroup.getController() != null) {
            this.controllers.add(endpointGroup.getController());
        }
        for (EndpointGroup innerGroup : endpointGroup.getInnerGroups()) {
            updateControllers(innerGroup);
        }
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

    private static final DataField request = new DataField(REQUEST, "request");
    private static final DataField response = new DataField(RESPONSE, "response");

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator classGenerator = new ClassGenerator(fileWriter, this);
        classGenerator.generateClass(() -> {
            for (Controller controller : this.controllers) {
                classGenerator.generateImport(controller.getImportString());
            }
            classGenerator.generateStaticImport("spark.Spark.*");
        }, () -> {
            classGenerator.generateMethod("start", VOID, null, () -> {
                if (this.endpointGroups.isEmpty()) {
                    writeEndpoints(fileWriter, classGenerator,null,"", this.endpoints);
                }
                else {
                    recursiveEndpointGroups(fileWriter, classGenerator, this.endpointGroups.get(0), "", 0);
                }
            });
        });
    }

    private void recursiveEndpointGroups(FileWriter fileWriter, ClassGenerator classGenerator, EndpointGroup endpointGroup, String groupPath, int index) throws IOException {
        if (!endpointGroup.getEndpoints().isEmpty()) {
            writeEndpoints(fileWriter, classGenerator, endpointGroup.getController(),groupPath + endpointGroup.getGroupName(), endpointGroup.getEndpoints());
        }
        for (EndpointGroup innerGroup : endpointGroup.getInnerGroups()) {
            recursiveEndpointGroups(fileWriter, classGenerator, innerGroup, groupPath + endpointGroup.getGroupName(), index);
        }
        ++index;
    }
    private void writeEndpoints(FileWriter fileWriter, ClassGenerator classGenerator, Controller controller, String groupName, List<Endpoint> endpoints) throws IOException {
        if (controller != null) {
            DataField.Type controllerType = DataField.Type.custom(controller.getName());
            fileWriter.write("\n");
            classGenerator.generateMethodVar(controller.getName(), controllerType, "new " + controllerType.type + "()");
            for (Endpoint endpoint : endpoints) {
                System.out.println(groupName + endpoint.path());
                String method = controller.addMethod(endpoint.linkedMethodName());
                fileWriter.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                        "\", " + controller.getName() + "::" + method + ");\n");
            }
        }
        else {
            fileWriter.write("\n");
            for (Endpoint endpoint : endpoints) {
                System.out.println(groupName + endpoint.path());
                fileWriter.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                        "\", ((req, res) -> \"" + endpoint.method().method + "\"));\n");
            }
        }
    }

    public static class RestBuilder implements IApiBuilder<Rest> {
        private final Rest rest;
        private RestBuilder() {
            this.rest = new Rest();
        }

        public RestBuilder endpoint(Endpoint.HttpMethod httpMethod, String path, String linkedMethodName) {
            return this.endpoint(new Endpoint(httpMethod, path, linkedMethodName));
        }
        public RestBuilder endpoint(Endpoint endpoint) {
            this.rest.addEndpoint(endpoint);
            return this;
        }
        public RestBuilder endpointGroup(EndpointGroup endpointGroup) {
            this.rest.addEndpointGroup(endpointGroup);
            return this;
        }

        @Override
        public Rest build() {
            return this.rest;
        }
    }
}
