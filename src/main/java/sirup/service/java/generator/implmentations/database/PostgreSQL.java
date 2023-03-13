package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.misc.DataModel;
import sirup.service.java.generator.interfaces.IDatabase;

import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.misc.DataField.Type.*;

public final class PostgreSQL extends AbstractDatabase implements IDatabase {

    private final List<DataModel> dataModels;

    public static final PostgreSQL DEFAULT;
    static {
        DEFAULT = new PostgreSQL();
        DEFAULT.addDataModel(DataModel.builder().name("default").dataField(INT32, "id").build());
    }

    private PostgreSQL() {
        this.dataModels = new ArrayList<>();
    }

    private void addDataModel(DataModel dataModel) {
        this.dataModels.add(dataModel);
    }

    public List<DataModel> getDataModels() {
        return this.dataModels;
    }

    public static PostgreSQLBuilder builder() {
        return new PostgreSQLBuilder();
    }

    @Override
    public String toString() {
        return "postgresql: {}";
    }

    @Override
    public String getName() {
        return "PostgresQL";
    }

    @Override
    public String getDependencyName() {
        return "PostgreSQL";
    }

    @Override
    public void generate() {

    }

    public static class PostgreSQLBuilder {
        private final PostgreSQL postgreSQL;
        private PostgreSQLBuilder() {
            this.postgreSQL = new PostgreSQL();
        }

        public PostgreSQLBuilder dataModel(DataModel.DataModelBuilder dataModelBuilder) {
            return this.dataModel(dataModelBuilder.build());
        }
        public PostgreSQLBuilder dataModel(DataModel dataModel) {
            this.postgreSQL.addDataModel(dataModel);
            return this;
        }

        public PostgreSQL build() {
            return this.postgreSQL;
        }
    }
}
