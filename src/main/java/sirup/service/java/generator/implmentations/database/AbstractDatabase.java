package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.Service;
import sirup.service.java.generator.interfaces.database.IDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabase extends AbstractGenerateable implements IDatabase {

    protected final List<Service> services;
    protected final List<DataModel> dataModels;

    public AbstractDatabase() {
        this.services = new ArrayList<>();
        this.dataModels = new ArrayList<>();
    }

    @Override
    public List<Service> getServices() {
        return this.services;
    }

    @Override
    public List<DataModel> getDataModels() {
        return this.dataModels;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".database";
    }
}
