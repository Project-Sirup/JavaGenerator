package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.classgeneration.Access;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.Service;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.Type.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;

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
                .classImports(importGenerator -> {
                    importGenerator.generateImport("java.sql.*");
                })
                .classBody(classGenerator -> {
                    classGenerator.generateAttribute(Access.PRIVATE, "connectionString", STRING.type, "\"jdbc:postgresql://localhost:5432/javagen\"");
                    classGenerator.generateAttribute(Access.PRIVATE,"user", STRING.type, "\"postgres\"");
                    classGenerator.generateAttribute(Access.PRIVATE,"password", STRING.type, "\"admin\"");
                    classGenerator.generateAttribute(Access.PRIVATE,"connection", CONNECTION.type, "null");
                    classGenerator.generateMethod("connect", BOOLEAN.type, null, () -> {
                        classGenerator.generateTryCatch(() -> {
                            classGenerator.write(tab(3) + "this.connection = DriverManager.getConnection(connectionString, user, password);\n");
                        }, "SQLException", () -> {
                            classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            classGenerator.write(tab(3) + "return false;\n");
                        });
                        classGenerator.write(tab(2) + "return true;\n");
                    });
                    classGenerator.generateMethod("disconnect", BOOLEAN.type, null, () -> {
                        classGenerator.generateTryCatch(() -> {
                            classGenerator.write(tab(3) + "this.connection.close();\n");
                        }, "SQLException", () -> {
                            classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            classGenerator.write(tab(3) + "return false;\n");
                        });
                        classGenerator.write(tab(2) + "return true;\n");
                    });
                    classGenerator.generateMethod("getConnection", "Connection", null, () -> {
                        classGenerator.write(tab(2) + "return this.connection;\n");
                    });
                })
                .build()
                .make();
    }

    @Override
    public Generateable getDbInit() {
        return new Sql(this.dataModels);
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

    public static class Sql extends AbstractGenerateable {

        private final List<DataModel> dataModels;

        private Sql(final List<DataModel> dataModels) {
            this.dataModels = dataModels;
        }

        @Override
        public String getPackageName() {
            return "";
        }

        @Override
        public void fillFile(FileWriter fileWriter) throws IOException {
            for (DataModel dataModel : this.dataModels) {
                fileWriter.write("CREATE TABLE " + uncapitalize(dataModel.getName()) + "s (\n");
                fileWriter.write(tab(1) + dataModel.getDataFields().get(0).name() + " " + toSqlType(dataModel.getDataFields().get(0).type()) + " UNIQUE NOT NULL,\n");
                for (int i = 1; i < dataModel.getDataFields().size(); i++) {
                    DataField dataField = dataModel.getDataFields().get(i);
                    String ref = dataField.ref() == null ? "" : " REFERENCES " + dataField.ref().replace(".", "s(") + ")";
                    fileWriter.write(tab(1) + dataField.name() + " " + toSqlType(dataField.type()) + ref + " NOT NULL,\n");
                }
                fileWriter.write(tab(1) + "PRIMARY KEY(" + dataModel.getDataFields().get(0).name() + ")\n");
                fileWriter.write(");\n");
            }
        }

        private String toSqlType(String type) {
            type = type.toLowerCase();
            switch (type) {
                case "string" -> {
                    return "VARCHAR(255)";
                }
                case "int", "int32", "integer" -> {
                    return "INTEGER";
                }
                case "bool", "boolean" -> {
                    return "BOOLEAN";
                }
                default -> throw new IllegalArgumentException("cannot make " + type + " into a sql type");
            }
        }

        @Override
        public String getName() {
            return "init.sql";
        }

        @Override
        public String getDir() {
            return "";
        }
    }
}
