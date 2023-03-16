package sirup.service.java.generator.api;

import java.util.ArrayList;
import java.util.List;

public record MicroserviceRequest(int sirup_v, boolean docker, Microservice microservice) {
    public record Microservice(String microserviceId, String microserviceName, Language language, Database database, Api api, External external) {
        public record Language(String name, Options options) {
            public record Options(String buildTool, String packageName) {}
        }
        public record Database(String name, Options options, Data data) {
            public record Options() {}
            public record Data(List<Collection> collections) {
                public record Collection(String name, List<Field> fields) {
                    public record Field(String name, String type) {}
                }
            }
        }
        public record Api(String type, boolean useAsDocs, List<EndpointGroup> endpointGroups, List<Endpoint> endpoints) {
            public record Endpoint(String method, String path) {}
            public record EndpointGroup(String groupName, List<EndpointGroup> innerGroups, List<Endpoint> endpoints, String linkedToData) {}
        }
        public record External(String name) {}
    }

    public static MicroserviceRequest test() {
        return new MicroserviceRequest(1, true,
                new MicroserviceRequest.Microservice(
                "asda", "Micro",
                        new Microservice.Language(
                        "Java",
                                new Microservice.Language.Options("Maven", "dk.sdu.mmmi")),
                        new Microservice.Database(
                        "PostgreSQL", new Microservice.Database.Options(
                                ),
                                new Microservice.Database.Data(
                                        new ArrayList<Microservice.Database.Data.Collection>(){{
                                            add(new Microservice.Database.Data.Collection("user",
                                                    new ArrayList<Microservice.Database.Data.Collection.Field>(){{
                                                        add(new Microservice.Database.Data.Collection.Field("userId", "string"));
                                                        add(new Microservice.Database.Data.Collection.Field("userName", "string"));}}));}})),
                        new Microservice.Api(
                        "Rest", true,
                                new ArrayList<Microservice.Api.EndpointGroup>(){{
                                    add(new Microservice.Api.EndpointGroup(
                                    "/api", new ArrayList<MicroserviceRequest.Microservice.Api.EndpointGroup>(){{
                                        add(new Microservice.Api.EndpointGroup(
                                           "/v1",
                                                new ArrayList<>(){{
                                                    add(new Microservice.Api.EndpointGroup(
                                                       "/user", null, new ArrayList<Microservice.Api.Endpoint>(){{
                                                           add(new Microservice.Api.Endpoint("get", ""));
                                                           add(new Microservice.Api.Endpoint("get", "/:userId"));
                                                           add(new Microservice.Api.Endpoint("post", ""));
                                                           add(new Microservice.Api.Endpoint("put", ""));
                                                           add(new Microservice.Api.Endpoint("delete", "/:userId"));}}, "user"));}}, null, null));}}, null, null));}}, null),
                        new Microservice.External("external")));
    }
}
