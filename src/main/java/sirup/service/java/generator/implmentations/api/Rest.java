package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.common.*;
import sirup.service.java.generator.implmentations.common.classgeneration.Access;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.api.IApiBuilder;
import sirup.service.java.generator.interfaces.common.DockerService;
import sirup.service.java.generator.interfaces.common.Generateable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.Endpoint.HttpMethod.*;
import static sirup.service.java.generator.implmentations.common.Type.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public final class Rest extends AbstractApi {

    private final List<Endpoint> endpoints;
    private final List<EndpointGroup> endpointGroups;
    private Map<String,DataModel> dataMap;

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

    private void setDataMap(Map<String,DataModel> dataMap) {
        this.dataMap = dataMap;
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
        return "rest";
    }

    private static final DataField request = new DataField(REQUEST.type, "request");
    private static final DataField response = new DataField(RESPONSE.type, "response");

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(importGenerator -> {
                    for (Controller controller : this.controllers) {
                        importGenerator.generateImport(controller.getImportString());
                    }
                    importGenerator.generateStaticImport("spark.Spark.*");
                    importGenerator.generateImport(this.context.getImportString());
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

    private List<Endpoint> iterateEndpoints(
            List<MicroserviceRequest.Microservice.Api.Options.Endpoint> inputEndpoints) {
        List<Endpoint> endpoints = new ArrayList<>();
        if (inputEndpoints != null) {
            inputEndpoints.forEach(inputEndpoint -> {
                endpoints.add(new Endpoint(Endpoint.HttpMethod.from(inputEndpoint.method()),
                        inputEndpoint.path(), inputEndpoint.linkedMethod()));
            });
        }
        return endpoints;
    }

    private EndpointGroup.EndpointGroupBuilder iterateEndpointGroups(
            MicroserviceRequest.Microservice.Api.Options.EndpointGroup endpointGroup, int index) {
        EndpointGroup.EndpointGroupBuilder endpointGroupBuilder = EndpointGroup.builder();
        endpointGroupBuilder.groupName(endpointGroup.groupName());
        if (endpointGroup.endpoints() != null && !endpointGroup.endpoints().isEmpty()) {
            addEndpoints(endpointGroupBuilder, endpointGroup.endpoints());
        }
        if (endpointGroup.linkedData() != null) {
            endpointGroupBuilder.controller(Controller.of(this.dataMap.get(endpointGroup.linkedData())));
        }
        if (endpointGroup.innerGroups() != null && !endpointGroup.innerGroups().isEmpty()) {
            for (MicroserviceRequest.Microservice.Api.Options.EndpointGroup innerGroup : endpointGroup.innerGroups()) {
                EndpointGroup.EndpointGroupBuilder innerGroupBuilder = iterateEndpointGroups(innerGroup, index);
                endpointGroupBuilder.innerGroup(innerGroupBuilder.build());
            }
        }
        ++index;
        return endpointGroupBuilder;
    }

    private void addEndpoints(EndpointGroup.EndpointGroupBuilder endpointGroupBuilder,
                              List<MicroserviceRequest.Microservice.Api.Options.Endpoint> endpoints) {
        for (MicroserviceRequest.Microservice.Api.Options.Endpoint endpoint : endpoints) {
            endpointGroupBuilder.endpoint(Endpoint.HttpMethod.from(endpoint.method()),
                    endpoint.path(), endpoint.linkedMethod());
        }
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
                String method = controller.addMethod(endpoint.linkedMethodName(), endpoint.method().method);
                classGenerator.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                        "\", " + controller.getName() + "::" + method + ");\n");
            }
        }
        else {
            classGenerator.write("\n");
            for (Endpoint endpoint : endpoints) {
                classGenerator.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                        "\", ((req, res) -> \"" + endpoint.method().method + "\"));\n");
            }
        }
    }

    @Override
    public Generateable getDockerfile() {
        return new RestDockerfile();
    }

    @Override
    public DockerService getDockerService() {
        return new RestDockerService();
    }

    public static class RestBuilder implements IApiBuilder<Rest> {
        private final Rest rest;
        private MicroserviceRequest.Microservice.Api.Options options;
        private RestBuilder() {
            this.rest = new Rest();
        }

        @Override
        public IApiBuilder<Rest> options(MicroserviceRequest.Microservice.Api.Options options) {
            this.options = options;
            this.rest.setPort(options.port());
            this.rest.addEndpoints(this.rest.iterateEndpoints(options.endpoints()));
            return this;
        }

        @Override
        public IApiBuilder<Rest> dataMap(Map<String, DataModel> dataMap) {
            this.rest.setDataMap(dataMap);
            return this;
        }

        @Override
        public Rest build() {
            if (options.endpointGroups() != null && this.rest.dataMap != null) {
                this.rest.addEndpointGroup(this.rest.iterateEndpointGroups(options.endpointGroups().get(0),0).build());
            }
            return this.rest;
        }
    }

    public static class RestDockerfile extends AbstractDockerfile {

        @Override
        public String getName() {
            return "rest.dockerfile";
        }

        @Override
        public void fillFile(FileWriter fileWriter) throws IOException {
            DockerGenerator.builder()
                    .from("java")
                    .workdir("/app/api")
                    .copy("./target", ".")
                    .entrypoint("java", "-jar", "")
                    .build()
                    .make();

        }
    }

    public static class RestDockerService implements DockerService {

        @Override
        public String getName() {
            return "rest";
        }

        @Override
        public int getInternalPort() {
            return 2233;
        }

        @Override
        public int getExternalPort() {
            return 2233;
        }

        @Override
        public String getBuildContext() {
            return "./rest.dockerfile";
        }
    }
}
