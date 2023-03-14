package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.DataModel;
import sirup.service.java.generator.interfaces.database.IDatabase;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.DataField.Type.*;
import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;

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

    static PostgreSQLBuilder builder() {
        return new PostgreSQLBuilder();
    }

    @Override
    public String toString() {
        return "postgresql: {}";
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }

    @Override
    public String getDependencyName() {
        return "PostgreSQL";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        fileWriter.write(packageString(this.getPackageName()));
        fileWriter.write(importString("java.sql.*"));
        generateClass(fileWriter, this.getName(), () -> {
            generatePrivateAttribute(fileWriter, "connectionString", STRING, "\"jdbc:postgresql://localhost:5432/database\"");
            generatePrivateAttribute(fileWriter, "user", STRING, "\"postgres\"");
            generatePrivateAttribute(fileWriter, "password", STRING, "\"admin\"");
            generatePrivateAttribute(fileWriter, "connection", CONNECTION, "null");
            generateMethod(fileWriter, "connect", BOOLEAN, null, () -> {
                generateTryCatch(fileWriter, () -> {
                    fileWriter.write("\t\t\tthis.connection = DriverManager.getConnection(connectionString, user, password);\n");
                }, "SQLException", () -> {
                    fileWriter.write("\t\t\te.printStackTrace();\n");
                    fileWriter.write("\t\t\treturn false;\n");
                });
                fileWriter.write("\t\treturn true;\n");
            });
            generateMethod(fileWriter, "disconnect", BOOLEAN, null, () -> {
                generateTryCatch(fileWriter, () -> {
                    fileWriter.write("\t\t\tthis.connection.close();\n");
                }, "SQLException", () -> {
                    fileWriter.write("\t\t\te.printStackTrace();\n");
                    fileWriter.write("\t\t\treturn false;\n");
                });
                fileWriter.write("\t\treturn true;\n");
            });
        });
    }

    public static class PostgreSQLBuilder implements IDatabaseBuilder<IDatabase> {
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

        @Override
        public PostgreSQL build() {
            return this.postgreSQL;
        }
    }
}
