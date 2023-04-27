package sirup.service.java.generator.interfaces.api;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.interfaces.common.Builder;

public interface IApiBuilder<T> extends Builder<T> {
    IApiBuilder<T> options(MicroserviceRequest.Microservice.Api.Options options);
}
