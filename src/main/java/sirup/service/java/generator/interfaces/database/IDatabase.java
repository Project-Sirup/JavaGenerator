package sirup.service.java.generator.interfaces.database;

import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.Service;
import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.common.Nameable;

import java.util.List;

public interface IDatabase extends Nameable, Dependency, Generateable {
    List<Service> getServices();
    List<DataModel> getDataModels();
}
