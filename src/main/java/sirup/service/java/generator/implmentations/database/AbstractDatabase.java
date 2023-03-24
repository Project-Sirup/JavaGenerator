package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.AbstractService;
import sirup.service.java.generator.interfaces.database.IDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabase extends AbstractGenerateable implements IDatabase {

    protected final List<AbstractService> services;
    protected final List<DataModel> dataModels;

    public AbstractDatabase() {
        this.services = new ArrayList<>();
        this.dataModels = new ArrayList<>();
        this.packageName = ".database";
    }

    @Override
    public List<AbstractService> getServices() {
        return this.services;
    }

    @Override
    public List<DataModel> getDataModels() {
        return this.dataModels;
    }
}
