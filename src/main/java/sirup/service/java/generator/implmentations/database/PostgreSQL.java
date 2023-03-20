package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.classgeneration.Access;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.Service;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.Type.*;

public final class PostgreSQL extends AbstractDatabase {

    public static final PostgreSQL DEFAULT;
    static {
        DEFAULT = new PostgreSQL();
        DEFAULT.addDataModel(DataModel.builder().name("default").dataField(INT32.type, "id").build());
    }

    private PostgreSQL() {
    }

    private void addDataModel(DataModel dataModel) {
        this.dataModels.add(dataModel);
    }

    private void addDataModels(List<DataModel> dataModels) {
        this.dataModels.addAll(dataModels);
    }

    private void addService(Service service) {
        this.services.add(service);
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
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(classGenerator -> {
                    classGenerator.generateImport("java.sql.*");
                })
                .classBody((classGenerator -> {
                    classGenerator.generateAttribute(Access.PRIVATE, "connectionString", STRING.type, "\"jdbc:postgresql://localhost:5432/javagen\"");
                    classGenerator.generateAttribute(Access.PRIVATE,"user", STRING.type, "\"postgres\"");
                    classGenerator.generateAttribute(Access.PRIVATE,"password", STRING.type, "\"admin\"");
                    classGenerator.generateAttribute(Access.PRIVATE,"connection", CONNECTION.type, "null");
                    classGenerator.generateMethod("connect", BOOLEAN.type, null, () -> {
                        classGenerator.generateTryCatch(() -> {
                            classGenerator.write("\t\t\tthis.connection = DriverManager.getConnection(connectionString, user, password);\n");
                        }, "SQLException", () -> {
                            classGenerator.write("\t\t\te.printStackTrace();\n");
                            classGenerator.write("\t\t\treturn false;\n");
                        });
                        classGenerator.write("\t\treturn true;\n");
                    });
                    classGenerator.generateMethod("disconnect", BOOLEAN.type, null, () -> {
                        classGenerator.generateTryCatch(() -> {
                            classGenerator.write("\t\t\tthis.connection.close();\n");
                        }, "SQLException", () -> {
                            classGenerator.write("\t\t\te.printStackTrace();\n");
                            classGenerator.write("\t\t\treturn false;\n");
                        });
                        classGenerator.write("\t\treturn true;\n");
                    });
                    classGenerator.generateMethod("getConnection", "Connection", null, () -> {
                        classGenerator.write("\t\treturn this.connection;\n");
                    });
                }))
                .build()
                .make();
    }

    public static class PostgreSQLBuilder implements IDatabaseBuilder<PostgreSQL> {
        private final PostgreSQL postgreSQL;
        private PostgreSQLBuilder() {
            this.postgreSQL = new PostgreSQL();
        }

        public PostgreSQLBuilder dataModel(DataModel.DataModelBuilder dataModelBuilder) {
            return this.dataModel(dataModelBuilder.build());
        }
        public PostgreSQLBuilder dataModel(DataModel dataModel) {
            this.postgreSQL.addDataModel(dataModel);
            this.postgreSQL.addService(Service.of(dataModel, this.postgreSQL));
            return this;
        }

        @Override
        public IDatabaseBuilder<PostgreSQL> dataModels(List<DataModel> dataModels) {
            this.postgreSQL.addDataModels(dataModels);
            for (DataModel dataModel : dataModels) {
                this.postgreSQL.addService(Service.of(dataModel, this.postgreSQL));
            }
            return this;
        }

        @Override
        public PostgreSQL build() {
            return this.postgreSQL;
        }
    }
}
