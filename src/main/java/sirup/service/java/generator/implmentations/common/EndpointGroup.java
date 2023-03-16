package sirup.service.java.generator.implmentations.common;

import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.interfaces.common.Builder;

import java.util.ArrayList;
import java.util.List;

public final class EndpointGroup {
    private String groupName;
    private final List<EndpointGroup> innerGroups;
    private final List<Endpoint> endpoints;
    private Controller controller;

    private EndpointGroup() {
        this.endpoints = new ArrayList<>();
        this.innerGroups = new ArrayList<>();
    }

    private void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    private void addInnerGroup(EndpointGroup innerGroup) {
        this.innerGroups.add(innerGroup);
    }

    public List<EndpointGroup> getInnerGroups() {
        return this.innerGroups;
    }

    private void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    public Controller getController() {
        return this.controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public List<Endpoint> getEndpoints() {
        return this.endpoints;
    }

    public static EndpointGroupBuilder builder() {
        return new EndpointGroupBuilder();
    }

    public static class EndpointGroupBuilder implements Builder<EndpointGroup> {
        private final EndpointGroup endpointGroup;

        private EndpointGroupBuilder() {
            this.endpointGroup = new EndpointGroup();
        }

        public EndpointGroupBuilder groupName(String groupName) {
            this.endpointGroup.setGroupName(groupName);
            return this;
        }

        public EndpointGroupBuilder controller(Controller controller) {
            this.endpointGroup.setController(controller);
            return this;
        }

        public EndpointGroupBuilder innerGroup(EndpointGroup innerGroup) {
            this.endpointGroup.addInnerGroup(innerGroup);
            return this;
        }

        public EndpointGroupBuilder endpoint(Endpoint.HttpMethod httpMethod, String path, String linkedMethodName) {
            return this.endpoint(new Endpoint(httpMethod, path, linkedMethodName));
        }
        public EndpointGroupBuilder endpoint(Endpoint endpoint) {
            this.endpointGroup.addEndpoint(endpoint);
            return this;
        }

        @Override
        public EndpointGroup build() {
            return this.endpointGroup;
        }
    }
}
