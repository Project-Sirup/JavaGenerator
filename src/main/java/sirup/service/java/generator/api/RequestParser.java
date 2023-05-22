package sirup.service.java.generator.api;

import com.google.gson.Gson;
import sirup.service.java.generator.implmentations.microservice.LanguageNotSupportedException;
import sirup.service.java.generator.implmentations.microservice.Microservice;
import sirup.service.java.generator.implmentations.api.Apis;
import sirup.service.java.generator.implmentations.buildtool.BuildTools;
import sirup.service.java.generator.implmentations.database.Databases;
import sirup.service.java.generator.implmentations.model.DataModel;

import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final Gson GSON = new Gson();

    public static Microservice fromJsonRequest(MicroserviceRequest m) {
        Map<String, DataModel> dataModelMap = new HashMap<>();
        m.microservice().database().data().collections().forEach(collection -> {
            DataModel.DataModelBuilder dataModelBuilder = DataModel.builder();
            dataModelBuilder.name(collection.name());
            if (collection.fields() != null) {
                collection.fields().forEach(field -> {
                    dataModelBuilder.dataField(field.type(), field.name(), field.ref());
                });
            }
            dataModelMap.put(collection.name(), dataModelBuilder.build());
        });
        String lang = m.microservice().language().name().toLowerCase();
        if (!lang.equals("java")) {
            throw new LanguageNotSupportedException("Language [" + lang + "] is not supported");
        }

        return Microservice.builder()
                .id(m.microservice().microserviceId())
                .docker(m.docker())
                .name(m.microservice().microserviceName())
                .groupId(m.microservice().language().options().groupId())
                .api(Apis.ofType(m.microservice().api().type())
                        .dataMap(dataModelMap)
                        .options(m.microservice().api().options())
                )
                .database(Databases.ofType(m.microservice().database().name())
                        .dataModels(dataModelMap.values().stream().toList())
                        .options(m.microservice().database().options())
                )
                .buildTool(BuildTools.ofType(m.microservice().language().options().buildTool()))
                .build();
    }
    public static Microservice fromJsonRequest(String json) {
        return RequestParser.fromJsonRequest(GSON.fromJson(json, MicroserviceRequest.class));
    }
}
