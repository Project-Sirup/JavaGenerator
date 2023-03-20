package sirup.service.java.generator.interfaces.database;

import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Builder;

import java.util.List;

public interface IDatabaseBuilder<T extends IDatabase> extends Builder<T> {
    IDatabaseBuilder<T> dataModel(DataModel dataMOdel);
    IDatabaseBuilder<T> dataModels(List<DataModel> dataModels);
}
