package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.common.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.Endpoint;
import sirup.service.java.generator.implmentations.common.EndpointGroup;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;
import static sirup.service.java.generator.implmentations.common.Endpoint.Method.*;
import static sirup.service.java.generator.implmentations.common.DataField.Type.*;

public final class Rest extends AbstractApi implements IApi {

    private final List<Endpoint> endpoints;
    private final List<EndpointGroup> endpointGroups;

    public static final Rest DEFAULT;
    static {
        DEFAULT = new Rest();
        EndpointGroup baseGroup = EndpointGroup
                .builder()
                .groupName("/api/v1")
                .endpoint(new Endpoint(GET, ""))
                .build();
        DEFAULT.addEndpointGroup(baseGroup);
    }

    private Rest() {
        this.endpoints = new ArrayList<>();
        this.endpointGroups = new ArrayList<>();
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
        fileWriter.write(packageString(this.getPackageName()));
        fileWriter.write(staticImportString("spark.Spark.*"));
        generateClass(fileWriter, this.getName(), () -> {
            generateMethod(fileWriter, "start", VOID, null, () -> {
                if (this.endpointGroups.size() == 0) {
                    wrtieEndpoints(fileWriter, "", this.endpoints);
                }
                else {
                    recursiveEndpintGroups(fileWriter, this.endpointGroups.get(0), "", 0);
                }
            });
        });
    }

    private void recursiveEndpintGroups(FileWriter fileWriter, EndpointGroup endpointGroup, String groupPath, int index) throws IOException {
        System.out.println(groupPath);
        System.out.println(index + " " + endpointGroup.getInnerGroup().size());
        if (endpointGroup.getInnerGroup().size() == 0) {
            wrtieEndpoints(fileWriter, groupPath, endpointGroup.getEndpoints());
            return;
        }
        for (EndpointGroup innerGroup : endpointGroup.getInnerGroup()) {
            recursiveEndpintGroups(fileWriter, innerGroup, groupPath + endpointGroup.getGroupName(), index);
        }
        ++index;
    }
    private void wrtieEndpoints(FileWriter fileWriter, String groupName, List<Endpoint> endpoints) throws IOException {
        for (Endpoint endpoint : endpoints) {
            fileWriter.write("\t\t" + endpoint.method().method + "(\"" + groupName + endpoint.path() +
                    "\", ((request, response) -> \" " + endpoint.method().method + "\"));\n");
        }
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
        public RestBuilder endpontGroup(EndpointGroup endpointGroup) {
            this.rest.addEndpointGroup(endpointGroup);
            return this;
        }

        @Override
        public Rest build() {
            return this.rest;
        }
    }
}
