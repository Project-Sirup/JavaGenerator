package sirup.service.java.generator.interfaces.api;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.implmentations.common.Endpoint;
import sirup.service.java.generator.implmentations.common.EndpointGroup;
import sirup.service.java.generator.interfaces.common.Builder;

import java.util.List;

public interface IApiBuilder<T> extends Builder<T> {
    //IApiBuilder<T> port(int port);
    IApiBuilder<T> options(MicroserviceRequest.Microservice.Api.Options options);
    //IApiBuilder<T> endpoint(Endpoint.HttpMethod httpMethod, String path, String linkedMethodName);
    //IApiBuilder<T> endpoints(List<Endpoint> endpoints);
    //IApiBuilder<T> endpoint(Endpoint endpoint);
    //IApiBuilder<T> endpointGroup(EndpointGroup endpointGroup);
    //IApiBuilder<T> endpointGroup(EndpointGroup.EndpointGroupBuilder endpointGroupBuilder);
}
