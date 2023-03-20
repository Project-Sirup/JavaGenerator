package sirup.service.java.generator.api;

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
        public record Api(String type, Options options) {
            public record Options(int port, List<EndpointGroup> endpointGroups, List<Endpoint> endpoints) {
                public record Endpoint(String method, String path, String linkedMethod) {}
                public record EndpointGroup(String groupName, List<EndpointGroup> innerGroups, List<Endpoint> endpoints, String linkedData) {}
            }
        }
        public record External(String name) {}
    }
}
