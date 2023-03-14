package sirup.service.java.generator.implmentations.common;

import sirup.service.java.generator.interfaces.common.Builder;

import java.util.ArrayList;
import java.util.List;

public final class EndpointGroup {
    private String groupName;
    private final List<EndpointGroup> innerGroup;
    private final List<Endpoint> endpoints;

    private EndpointGroup() {
        this.endpoints = new ArrayList<>();
        this.innerGroup = new ArrayList<>();
    }

    private void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getGroupName() {
        return this.groupName;
    }
    private void addInnerGroup(EndpointGroup innerGroup) {
        this.innerGroup.add(innerGroup);
    }
    public List<EndpointGroup> getInnerGroup() {
        return this.innerGroup;
    }

    private void addEndpoint(Endpoint endpoint) {
        this.endpoints.add(endpoint);
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

        public EndpointGroupBuilder innerGrouo(EndpointGroup innerGrouo) {
            this.endpointGroup.addInnerGroup(innerGrouo);
            return this;
        }

        public EndpointGroupBuilder endpoint(Endpoint.Method method, String path) {
            return this.endpoint(new Endpoint(method, path));
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
