package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.classgeneration.*;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.service.AbstractService;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.database.IDatabase;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private void addService(AbstractService service) {
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
            this.postgreSQL.addService(PostgreSqlService.of(dataModel, this.postgreSQL));
            return this;
        }

        @Override
        public IDatabaseBuilder<PostgreSQL> dataModels(List<DataModel> dataModels) {
            this.postgreSQL.addDataModels(dataModels);
            for (DataModel dataModel : dataModels) {
                this.postgreSQL.addService(PostgreSqlService.of(dataModel, this.postgreSQL));
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
                if (dataModel.getDataFields() == null || dataModel.getDataFields().isEmpty()) {
                    continue;
                }
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

    public static class PostgreSqlService extends AbstractService {

        public PostgreSqlService(DataModel dataModel, IDatabase database) {
            super(dataModel, database);
        }

        public static AbstractService of(DataModel dataModel, IDatabase database) {
            return new PostgreSqlService(dataModel,database);
        }

        @Override
        public void fillFile(FileWriter fileWriter) throws IOException {
            String[] splitString = this.getImportString().split("\\.");
            splitString[splitString.length -2] = "interfaces";
            splitString[splitString.length -1] = "Service";
            String interfaceImportString = String.join(".", splitString);

            ClassGenerator.builder()
                    .fileWriter(fileWriter)
                    .generateable(this)
                    .classType(ClassTypes.CLASS())
                    .implement("Service<" + this.dataModel.getName() + ">")
                    .classImports(importGenerator -> {
                        importGenerator.generateImport("java.sql.*;");
                        importGenerator.generateImport(Imports.LIST);
                        importGenerator.generateImport(Imports.ARRAY_LIST);
                        importGenerator.generateImport(this.dataModel.getImportString());
                        importGenerator.generateImport(this.database.getImportString());
                        importGenerator.generateImport(interfaceImportString);
                    })
                    .classBody(classGenerator -> {
                        classGenerator.generateAttribute(Access.PRIVATE, "connection", "Connection", "null");
                        classGenerator.generateAnnotation(Annotations.OVERRIDE);
                        classGenerator.generateMethod("addDatabase", VOID.type, new ArrayList<>(){{
                            add(new DataField(database.getName(), "database"));
                        }}, () -> {
                            classGenerator.write(tab(2) + "this.connection = database.getConnection();\n");
                        });
                        String t = this.dataModel.getName();
                        String typeName = uncapitalize(this.dataModel.getName());
                        List<DataField> typeArg = new ArrayList<>(){{
                            add(new DataField(t, typeName));
                        }};
                        List<DataField> idArg = new ArrayList<>(){{
                            add(new DataField(STRING.type, "id"));
                        }};
                        classGenerator.generateAnnotation(Annotations.OVERRIDE);
                        classGenerator.generateMethod("add", BOOLEAN.type, typeArg, () -> {
                            if (this.dataModel.getDataFields() == null || this.dataModel.getDataFields().isEmpty()) {
                                return;
                            }
                            classGenerator.generateTryCatch(() -> {
                                String insertStatement = "\"INSERT INTO " + uncapitalize(this.dataModel.getName()) + "s (" +
                                        this.dataModel.getDataFields().stream().map(DataField::name).collect(Collectors.joining(", ")) +
                                        ") VALUES (" + this.dataModel.getDataFields().stream().map(dataField -> "?").collect(Collectors.joining(", ")) + ");\"";
                                classGenerator.write(tab(3) + "PreparedStatement statement = this.connection.prepareStatement(" + insertStatement + ");\n");
                                for (int i = 0; i < this.dataModel.getDataFields().size(); i++) {
                                    DataField dataField = this.dataModel.getDataFields().get(i);
                                    classGenerator.write(tab(3) + "statement.set" +
                                            convertToInsertType(dataField.type()) + "(" + (i + 1) + ", " + typeName + "." + dataField.name() + "());\n");
                                }
                                classGenerator.write(tab(3) + "statement.execute();\n");
                                classGenerator.write(tab(3) + "return true;\n");
                            }, "SQLException", () -> {
                                //TODO: add exception handling
                                classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            });
                            classGenerator.write(tab(2) + "return false;\n");
                        });
                        classGenerator.generateAnnotation(Annotations.OVERRIDE);
                        classGenerator.generateMethod("getAll", "List<" + t + ">", null, () -> {
                            classGenerator.generateTryCatch(() -> {
                                classGenerator.generateMethodVar("list", "\tList<" + t + ">", "new ArrayList<>()");
                                String getAllStatement = "\"SELECT * FROM " + uncapitalize(this.dataModel.getName()) + "s;\"";
                                classGenerator.write(tab(3) + "PreparedStatement statement = this.connection.prepareStatement(" + getAllStatement + ");\n");
                                classGenerator.write(tab(3) + "ResultSet resultSet = statement.executeQuery();\n");
                                classGenerator.write(tab(3) + "while(resultSet.next()) {\n");
                                classGenerator.write(tab(4) + this.dataModel.getName() + " " + uncapitalize(this.dataModel.getName()) +
                                        " = new " + this.dataModel.getName() + "(\n" + tab(5) +
                                        this.dataModel.getDataFields().stream().map(dataField ->
                                                "resultSet.get" + convertToInsertType(dataField.type()) + "(\"" + dataField.name() + "\")").collect(Collectors.joining(",\n" + tab(5))) +
                                        ");\n");
                                classGenerator.write(tab(4) + "list.add(" + uncapitalize(this.dataModel.getName()) + ");\n");
                                classGenerator.write(tab(3) + "}\n");
                                classGenerator.write(tab(3) + "return list;\n");
                            }, "SQLException", () -> {
                                classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            });
                            classGenerator.write(tab(2) + "return null;\n");
                        });
                        classGenerator.generateAnnotation(Annotations.OVERRIDE);
                        classGenerator.generateMethod("get", t, idArg, () -> {
                            if (this.dataModel.getDataFields() == null || this.dataModel.getDataFields().isEmpty()) {
                                return;
                            }
                            classGenerator.generateTryCatch(() -> {
                                String getStatement = "\"SELECT * FROM " + uncapitalize(this.dataModel.getName()) +
                                        "s WHERE " + this.dataModel.getDataFields().get(0).name() + " = ?;\"";
                                classGenerator.write(tab(3) + "PreparedStatement statement = this.connection.prepareStatement(" + getStatement + ");\n");
                                classGenerator.write(tab(3) + "statement.set" +
                                        convertToInsertType(this.dataModel.getDataFields().get(0).type()) + "(1, id);\n");
                                classGenerator.write(tab(3) + "ResultSet resultSet = statement.executeQuery();\n");
                                classGenerator.write(tab(3) + "resultSet.next();\n");
                                classGenerator.write(tab(3) + this.dataModel.getName() + " " + uncapitalize(this.dataModel.getName()) + "" +
                                        " = new " + this.dataModel.getName() + "(\n" + tab(5) + this.dataModel.getDataFields().stream().map(dataField ->
                                        "resultSet.get" + convertToInsertType(dataField.type()) + "(\"" + dataField.name() + "\")").collect(Collectors.joining(",\n" + tab(5))) +
                                        ");\n");
                                classGenerator.write(tab(3) + "return " + uncapitalize(this.dataModel.getName()) + ";\n");
                            }, "SQLException", () -> {
                                classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            });
                            classGenerator.write(tab(2) + "return null;\n");
                        });
                        classGenerator.generateAnnotation(Annotations.OVERRIDE);
                        classGenerator.generateMethod("update", BOOLEAN.type, typeArg, () -> {
                            if (this.dataModel.getDataFields() == null || this.dataModel.getDataFields().isEmpty()) {
                                return;
                            }
                            classGenerator.generateTryCatch(() -> {
                                List<String> setStrings = new ArrayList<>();
                                for (int i = 1; i < this.dataModel.getDataFields().size(); i++) {
                                    setStrings.add(this.dataModel.getDataFields().get(i).name() + " = ?");
                                }
                                String updateStatement = "\"UPDATE " + uncapitalize(this.dataModel.getName()) + "s SET " + String.join(", ", setStrings) + " WHERE " + this.dataModel.getDataFields().get(0).name() + " = ?;\"";
                                classGenerator.write(tab(3) + "PreparedStatement statement = this.connection.prepareStatement(" + updateStatement + ");\n");
                                for (int i = 1; i < this.dataModel.getDataFields().size(); i++) {
                                    classGenerator.write(tab(3) + "statement.set" +
                                            convertToInsertType(this.dataModel.getDataFields().get(i).type()) + "(" + i + ", " +
                                            uncapitalize(this.dataModel.getName()) + "." + this.dataModel.getDataFields().get(i).name() + "());\n");
                                }
                                classGenerator.write(tab(3) + "statement.set" +
                                        convertToInsertType(this.dataModel.getDataFields().get(0).type()) + "(" + this.dataModel.getDataFields().size() + ", " +
                                        uncapitalize(this.dataModel.getName()) + "." + this.dataModel.getDataFields().get(0).name() + "());\n");
                                classGenerator.write(tab(3) + "return statement.executeUpdate() == 1;\n");
                            }, "SQLException", () -> {
                                classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            });
                            classGenerator.write("\t\treturn false;\n");
                        });
                        classGenerator.generateAnnotation(Annotations.OVERRIDE);
                        classGenerator.generateMethod("remove", BOOLEAN.type, idArg, () -> {
                            if (this.dataModel.getDataFields() == null || this.dataModel.getDataFields().isEmpty()) {
                                return;
                            }
                            classGenerator.generateTryCatch(() -> {
                                String deleteStatemnet = "\"DELETE FROM " + uncapitalize(this.dataModel.getName()) + "s WHERE " +
                                        this.dataModel.getDataFields().get(0).name() + " = ?;\"";
                                classGenerator.write(tab(3) + "PreparedStatement statement = this.connection.prepareStatement(" + deleteStatemnet + ");\n");
                                classGenerator.write(tab(3) + "statement.set" + convertToInsertType(this.dataModel.getDataFields().get(0).type()) +
                                        "(1, id);\n");
                                classGenerator.write(tab(3) + "return statement.executeUpdate() == 1;\n");
                            }, "SQLException", () -> {
                                classGenerator.write(tab(3) + "e.printStackTrace();\n");
                            });
                            classGenerator.write(tab(2) + "return false;\n");
                        });
                    })
                    .build()
                    .make();
        }

        private String convertToInsertType(String inputType) {
            inputType = inputType.toLowerCase();
            switch (inputType) {
                default -> throw new IllegalArgumentException(inputType + " can not be converted to a SQL type");
                case "string", "varchar(255)" -> {
                    return "String";
                }
                case "int", "integer" -> {
                    return "Int";
                }
                case "bool", "boolean" -> {
                    return "Boolean";
                }
            }
        }
    }
}
