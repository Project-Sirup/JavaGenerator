package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.api.RequestParser;
import sirup.service.java.generator.implmentations.common.*;
import sirup.service.java.generator.implmentations.common.classgeneration.Access;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.Endpoint.HttpMethod.*;
import static sirup.service.java.generator.implmentations.common.Type.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;

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
        this.port = 4567;
    }

    @Override
    public String toString() {
        return "REST: {\n" +
                endpoints.stream().map(Endpoint::toString).collect(Collectors.joining("\n")) +
                "\n}";
    }

    private void setPort(int port) {
        this.port = port;
    }

    private void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    private void addEndpointGroup(EndpointGroup endpointGroup) {
        this.endpointGroups.add(endpointGroup);
        updateControllers(endpointGroup);
    }
    private void addEndpoints(List<Endpoint> endpoints) {
        this.endpoints.addAll(endpoints);
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

    private static final DataField request = new DataField(REQUEST.type, "request");
    private static final DataField response = new DataField(RESPONSE.type, "response");

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(classGenerator -> {
                    for (Controller controller : this.controllers) {
                        classGenerator.generateImport(controller.getImportString());
                    }
                    classGenerator.generateStaticImport("spark.Spark.*");
                    classGenerator.generateImport(this.context.getImportString());
                })
                .classBody(classGenerator -> {
                    classGenerator.generateAttribute(Access.PRIVATE, "context", "Context", "null");
                    classGenerator.generateConstructor(new ArrayList<>(){{add(new DataField("Context", "context"));}}, () -> {
                        classGenerator.write(tab(2) + "this.context = context;\n");
                    });
                    classGenerator.generateMethod("start", VOID.type, null, () -> {
                        classGenerator.write(tab(2) + "this.context.getDatabase().connect();\n");
                        classGenerator.write(tab(2) + "this.context.init();\n");
                        if (this.endpointGroups.isEmpty()) {
                            writeEndpoints(classGenerator,null,"", this.endpoints);
                        }
                        else {
                            recursiveEndpointGroups(classGenerator, this.endpointGroups.get(0), "", 0);
                        }
                    });
                })
                .build()
                .make();
    }

    private void recursiveEndpointGroups(ClassGenerator classGenerator, EndpointGroup endpointGroup, String groupPath, int index) throws IOException {
        if (!endpointGroup.getEndpoints().isEmpty()) {
            writeEndpoints(classGenerator, endpointGroup.getController(),groupPath + endpointGroup.getGroupName(), endpointGroup.getEndpoints());
        }
        for (EndpointGroup innerGroup : endpointGroup.getInnerGroups()) {
            recursiveEndpointGroups(classGenerator, innerGroup, groupPath + endpointGroup.getGroupName(), index);
        }
        ++index;
    }
    private void writeEndpoints(ClassGenerator classGenerator, Controller controller, String groupName, List<Endpoint> endpoints) throws IOException {
        if (controller != null) {
            classGenerator.write("\n");
            classGenerator.generateMethodVar(controller.getName(), controller.getName(), "new " + controller.getName() + "(this.context)");
            for (Endpoint endpoint : endpoints) {
                System.out.println(groupName + endpoint.path());
                String method = controller.addMethod(endpoint.linkedMethodName(), endpoint.method().method);
                classGenerator.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                        "\", " + controller.getName() + "::" + method + ");\n");
            }
        }
        else {
            classGenerator.write("\n");
            for (Endpoint endpoint : endpoints) {
                System.out.println(groupName + endpoint.path());
                classGenerator.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                        "\", ((req, res) -> \"" + endpoint.method().method + "\"));\n");
            }
        }
    }

    public static class RestBuilder implements IApiBuilder<Rest> {
        private final Rest rest;
        private RestBuilder() {
            this.rest = new Rest();
        }

        @Override
        public IApiBuilder<Rest> options(MicroserviceRequest.Microservice.Api.Options options) {
            this.rest.setPort(options.port());
            this.rest.addEndpoints(RequestParser.iterateEndpoints(options.endpoints()));
            this.rest.addEndpointGroup(RequestParser.iterateEndpointGroups(options.endpointGroups().get(0),0).build());
            return this;
        }

        @Override
        public Rest build() {
            return this.rest;
        }
    }
}
