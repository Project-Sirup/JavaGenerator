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
                        classGenerator.write("\t\tthis.connection = database.getConnection();\n");
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
                            System.out.println(insertStatement);
                            classGenerator.write("\t\t\tPreparedStatement statement = this.connection.prepareStatement(" + insertStatement + ");\n");
                            for (int i = 0; i < this.dataModel.getDataFields().size(); i++) {
                                DataField dataField = this.dataModel.getDataFields().get(i);
                                classGenerator.write("\t\t\tstatement.set" +
                                        convertToInsertType(dataField.type()) + "(" + (i + 1) + ", " + typeName + "." + dataField.name() + "());\n");
                            }
                            classGenerator.write("\t\tstatement.execute();\n");
                            classGenerator.write("\t\t\treturn true\n");
                        }, "SQLException", () -> {
                            //TODO: add exception handling
                            classGenerator.write("\t\t\te.printStackTrace();\n");
                        });
                        classGenerator.write("\t\treturn false;\n");
                    });
                    classGenerator.generateAnnotation(Annotations.OVERRIDE);
                    classGenerator.generateMethod("getAll", "List<" + t + ">", null, () -> {
                        classGenerator.generateTryCatch(() -> {
                            classGenerator.generateMethodVar("list", "\tList<" + t + ">", "new ArrayList<>()");
                            String getAllStatement = "\"SELECT * FROM " + uncapitalize(this.dataModel.getName()) + "s;\"";
                            classGenerator.write("\t\t\tPreparedStatement statement = this.connection.prepareStatement(" + getAllStatement + ");\n");
                            classGenerator.write("\t\t\tResultSet resultSet = statement.executeQuery();\n");
                            classGenerator.write("\t\t\twhile(resultSet.next()) {\n");
                            classGenerator.write("\t\t\t\t" + this.dataModel.getName() + " " + uncapitalize(this.dataModel.getName()) +
                                    " = new " + this.dataModel.getName() + "(\n\t\t\t\t\t" +
                                    this.dataModel.getDataFields().stream().map(dataField ->
                                            "resultSet.get" + convertToInsertType(dataField.type()) + "(\"" + dataField.name() + "\")").collect(Collectors.joining(",\n\t\t\t\t\t")) +
                                    ");\n");
                            classGenerator.write("\t\t\t\tlist.add(" + uncapitalize(this.dataModel.getName()) + ");\n");
                            classGenerator.write("\t\t\t}\n");
                            classGenerator.write("\t\t\treturn list;\n");
                        }, "SQLException", () -> {
                            classGenerator.write("\t\t\te.printStackTrace();\n");
                        });
                        classGenerator.write("\t\treturn null;\n");
                    });
                    classGenerator.generateAnnotation(Annotations.OVERRIDE);
                    classGenerator.generateMethod("get", t, idArg, () -> {
                        classGenerator.write("\t\treturn null;\n");
                    });
                    classGenerator.generateAnnotation(Annotations.OVERRIDE);
                    classGenerator.generateMethod("update", BOOLEAN.type, typeArg, () -> {
                        classGenerator.write("\t\treturn false;\n");
                    });
                    classGenerator.generateAnnotation(Annotations.OVERRIDE);
                    classGenerator.generateMethod("remove", BOOLEAN.type, idArg, () -> {
                        classGenerator.write("\t\treturn false;\n");
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
