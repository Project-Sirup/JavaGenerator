package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.misc.Endpoint;
import sirup.service.java.generator.interfaces.IApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.misc.Endpoint.Method.*;

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

    public static RestBuilder builder() {
        return new RestBuilder();
    }

    @Override
    public String getName() {
        return "REST";
    }

    @Override
    public String getDependencyName() {
        return "apache spark";
    }

    @Override
    public void generate() {

    }

    public static class RestBuilder {
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

        public Rest build() {
            return this.rest;
        }
    }
}
