package sirup.service.java.generator.interfaces.api;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Builder;
import sirup.service.java.generator.interfaces.common.OptionsStrategy;

import java.util.Map;

public interface IApiBuilder<T extends IApi> extends Builder<T>,
        OptionsStrategy<IApiBuilder<T>, MicroserviceRequest.Microservice.Api.Options> {
    IApiBuilder<T> dataMap(Map<String,DataModel> dataMap);
}
