package sirup.service.java.generator.implmentations.service;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.database.IDatabase;

import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public abstract class AbstractService extends AbstractGenerateable {

    protected final DataModel dataModel;
    protected final IDatabase database;

    public AbstractService(final DataModel dataModel, final IDatabase database) {
        this.dataModel = dataModel;
        this.database = database;
        this.packageName = ".services";
    }

    public DataModel getDataModel() {
        return this.dataModel;
    }

    @Override
    public String getName() {
        return capitalize(this.dataModel.getName()) + "Service";
    }
}
