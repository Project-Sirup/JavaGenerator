package sirup.service.java.generator.api;

import com.google.gson.Gson;
import sirup.service.java.generator.implmentations.microservice.LanguageNotSupportedException;
import sirup.service.java.generator.implmentations.microservice.Microservice;
import sirup.service.java.generator.implmentations.api.Apis;
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

    public static Microservice fromJsonRequest(MicroserviceRequest m) {
        DATA_MODEL_MAP.clear();
        m.microservice().database().data().collections().forEach(collection -> {
            DataModel.DataModelBuilder dataModelBuilder = DataModel.builder();
            dataModelBuilder.name(collection.name());
            if (collection.fields() != null) {
                collection.fields().forEach(field -> {
                    dataModelBuilder.dataField(field.type(), field.name(), field.ref());
                });
            }
            DATA_MODEL_MAP.put(collection.name(), dataModelBuilder.build());
        });
        String lang = m.microservice().language().name().toLowerCase();
        if (!lang.equals("java")) {
            throw new LanguageNotSupportedException("Language [" + lang + "] is not supported");
        }

        return Microservice.builder()
                .id(m.microservice().microserviceId())
                .name(m.microservice().microserviceName())
                .groupId(m.microservice()
                        .language()
                        .options().groupId())
                .api(Apis.ofType(m.microservice()
                                .api()
                                .type())
                        .options(m.microservice()
                                .api()
                                .options())
                )
                .database(Databases.ofType(m.microservice()
                                .database()
                                .name())
                        .dataModels(DATA_MODEL_MAP.values().stream().toList())
                        .options(m.microservice().database().options())
                )
                .buildTool(BuildTools.ofType(m.microservice()
                        .language()
                        .options()
                        .buildTool()))
                .build();
    }
    public static Microservice fromJsonRequest(String json) {
        return RequestParser.fromJsonRequest(GSON.fromJson(json, MicroserviceRequest.class));
    }

    public static List<Endpoint> iterateEndpoints(List<MicroserviceRequest.Microservice.Api.Options.Endpoint> inputEndpoints) {
        List<Endpoint> endpoints = new ArrayList<>();
        if (inputEndpoints != null) {
            inputEndpoints.forEach(inputEndpoint -> {
                endpoints.add(new Endpoint(Endpoint.HttpMethod.from(inputEndpoint.method()), inputEndpoint.path(), inputEndpoint.linkedMethod()));
            });
        }
        return endpoints;
    }

    public static EndpointGroup.EndpointGroupBuilder iterateEndpointGroups(MicroserviceRequest.Microservice.Api.Options.EndpointGroup endpointGroup, int index) {
        EndpointGroup.EndpointGroupBuilder endpointGroupBuilder = EndpointGroup.builder();
        endpointGroupBuilder.groupName(endpointGroup.groupName());
        if (endpointGroup.endpoints() != null && !endpointGroup.endpoints().isEmpty()) {
            addEndpoints(endpointGroupBuilder, endpointGroup.endpoints());
        }
        if (endpointGroup.linkedData() != null) {
            endpointGroupBuilder.controller(Controller.of(DATA_MODEL_MAP.get(endpointGroup.linkedData())));
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

    public static void addEndpoints(EndpointGroup.EndpointGroupBuilder endpointGroupBuilder, List<MicroserviceRequest.Microservice.Api.Options.Endpoint> endpoints) {
        for (MicroserviceRequest.Microservice.Api.Options.Endpoint endpoint : endpoints) {
            endpointGroupBuilder.endpoint(Endpoint.HttpMethod.from(endpoint.method()), endpoint.path(), endpoint.linkedMethod());
        }
    }
}
