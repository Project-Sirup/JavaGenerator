package sirup.service.java.generator.implmentations.service;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.Type;
import sirup.service.java.generator.implmentations.common.classgeneration.*;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.context.Context;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Contextable;
import sirup.service.java.generator.interfaces.database.IDatabase;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.StringUtil.*;
import static sirup.service.java.generator.implmentations.common.Type.*;

public class Service extends AbstractGenerateable implements Contextable {

    private final DataModel dataModel;
    private final IDatabase database;
    private Context context;

    public Service(final DataModel dataModel, final IDatabase database) {
        this.dataModel = dataModel;
        this.database = database;
    }

    public static Service of(DataModel dataModel, IDatabase database) {
        return new Service(dataModel,database);
    }

    public DataModel getDataModel() {
        return this.dataModel;
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
                .classImports(classGenerator -> {
                    classGenerator.generateImport("java.sql.*;");
                    classGenerator.generateImport(Imports.LIST);
                    classGenerator.generateImport(Imports.ARRAY_LIST);
                    classGenerator.generateImport(this.dataModel.getImportString());
                    classGenerator.generateImport(this.database.getImportString());
                    classGenerator.generateImport(interfaceImportString);
                })
                .classBody(classGenerator -> {
                    classGenerator.generateAttribute(Access.PRIVATE, "connection", "Connection", "null");
                    classGenerator.generateAnnotation(Annotations.OVERRIDE);
                    classGenerator.generateMethod("addDatabase", VOID.type, new ArrayList<>(){{add(new DataField(database.getName(), "database"));}}, () -> {
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

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".services";
    }

    @Override
    public String getName() {
        return capitalize(this.dataModel.getName()) + "Service";
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
