package sirup.service.java.generator.interfaces.database;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Builder;

import java.util.List;

public interface IDatabaseBuilder<T extends IDatabase> extends Builder<T> {
    IDatabaseBuilder<T> dataModels(List<DataModel> dataModels);
    IDatabaseBuilder<T> options(MicroserviceRequest.Microservice.Database.Options options);
}
