package sirup.service.java.generator.api;

import com.google.gson.Gson;
import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.implmentations.api.APIs;
import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.BuildTools;
import sirup.service.java.generator.implmentations.common.Endpoint;
import sirup.service.java.generator.implmentations.common.EndpointGroup;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.implmentations.database.Databases;
import sirup.service.java.generator.implmentations.model.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {

    private static final Gson GSON = new Gson();
    private static final Map<String, DataModel> DATA_MODEL_MAP = new HashMap<>();

    public static Microservice fromJsonRequest(String json) {
        MicroserviceRequest m = GSON.fromJson(json, MicroserviceRequest.class);

        DATA_MODEL_MAP.clear();
        m.microservice().database().data().collections().forEach(collection -> {
            DataModel.DataModelBuilder dataModelBuilder = DataModel.builder();
            dataModelBuilder.name(collection.name());
            collection.fields().forEach(field -> {
                dataModelBuilder.dataField(field.type(), field.name(), field.ref());
            });
            DATA_MODEL_MAP.put(collection.name(), dataModelBuilder.build());
        });

        return Microservice.builder()
                .id(m.microservice().microserviceId())
                .name(m.microservice().microserviceName())
                .packageName(m.microservice().language().options().packageName())
                .api(APIs.ofType(m.microservice().api().type())
                        .port(m.microservice().api().options().port())
                        .endpoints(iterateEndpoints(m.microservice().api().options().endpoints()))
                        .endpointGroup(iterateEndpointGroups(m.microservice().api().options().endpointGroups().get(0),0).build())
                )
                .database(Databases.ofType(m.microservice().database().name())
                        .dataModels(DATA_MODEL_MAP.values().stream().toList())
                )
                .buildTool(BuildTools.ofType(m.microservice().language().options().buildTool()))
                .build();
    }

    private static List<Endpoint> iterateEndpoints(List<MicroserviceRequest.Microservice.Api.Options.Endpoint> inputEndpoints) {
        List<Endpoint> endpoints = new ArrayList<>();
        inputEndpoints.forEach(inputEndpoint -> {
            endpoints.add(new Endpoint(Endpoint.HttpMethod.from(inputEndpoint.method()), inputEndpoint.path(), inputEndpoint.linkedMethod()));
        });
        return endpoints;
    }

    private static EndpointGroup.EndpointGroupBuilder iterateEndpointGroups(MicroserviceRequest.Microservice.Api.Options.EndpointGroup endpointGroup, int index) {
        EndpointGroup.EndpointGroupBuilder endpointGroupBuilder = EndpointGroup.builder();
        endpointGroupBuilder.groupName(endpointGroup.groupName());
        if (!endpointGroup.endpoints().isEmpty()) {
            addEndpoints(endpointGroupBuilder, endpointGroup.endpoints());
        }
        if (endpointGroup.linkedData() != null) {
            endpointGroupBuilder.controller(Controller.of(DATA_MODEL_MAP.get(endpointGroup.linkedData())));
        }
        for (MicroserviceRequest.Microservice.Api.Options.EndpointGroup innerGroup : endpointGroup.innerGroups()) {
            EndpointGroup.EndpointGroupBuilder innerGroupBuilder = iterateEndpointGroups(innerGroup, index);
            endpointGroupBuilder.innerGroup(innerGroupBuilder.build());
        }
        ++index;
        return endpointGroupBuilder;
    }

    private static void addEndpoints(EndpointGroup.EndpointGroupBuilder endpointGroupBuilder, List<MicroserviceRequest.Microservice.Api.Options.Endpoint> endpoints) {
        for (MicroserviceRequest.Microservice.Api.Options.Endpoint endpoint : endpoints) {
            System.out.println(endpoint.method() + endpoint.path());
            endpointGroupBuilder.endpoint(Endpoint.HttpMethod.from(endpoint.method()), endpoint.path(), endpoint.linkedMethod());
        }
    }
}
