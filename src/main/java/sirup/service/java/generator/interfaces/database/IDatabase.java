package sirup.service.java.generator.interfaces.database;

import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.AbstractService;
import sirup.service.java.generator.interfaces.common.Containerizable;
import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.common.Generateable;

import java.util.List;

public interface IDatabase extends Dependency, Generateable, Containerizable {
    List<AbstractService> getServices();
    List<DataModel> getDataModels();
    Generateable getDbInit();
}
