package sirup.service.java.generator.implmentations.model;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.StringUtil;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataModel extends AbstractGenerateable {
    private String name = "DEFAULT";
    private final List<DataField> dataFields;

    private DataModel() {
        this.dataFields = new ArrayList<>();
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return StringUtil.capitalize(this.name);
    }

    private void addDataField(DataField dataField) {
        this.dataFields.add(dataField);
    }

    public List<DataField> getDataFields() {
        return this.dataFields;
    }

    public static DataModelBuilder builder() {
        return new DataModelBuilder();
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.RECORD())
                .dataModel(this)
                .implement("Model")
                .classImports(classGenerator -> {
                    String[] splitString = this.getImportString().split("\\.");
                    splitString[splitString.length - 2] = "interfaces";
                    splitString[splitString.length - 1] = "Model";
                    String interfaceImportString = String.join(".", splitString);
                    classGenerator.generateImport(interfaceImportString);
                })
                .build()
                .make();
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".models";
    }

    public static class DataModelBuilder {
        private final DataModel dataModel;

        private DataModelBuilder() {
            this.dataModel = new DataModel();
        }

        public DataModelBuilder name(String name) {
            this.dataModel.setName(name);
            return this;
        }

        public DataModelBuilder dataField(String type, String name) {

            return this.dataField(new DataField(convertType(type), name));
        }
        private String convertType(String inputType) {
            String outputType = inputType = inputType.toLowerCase();
            switch (outputType) {
                case "string" -> {
                    return "String";
                }
                case "int32" -> {
                    return "int";
                }
                case "int64" -> {
                    return "long";
                }
                case "bool" -> {
                    return "boolean";
                }
                default -> {
                    return inputType;
                }
            }
        }
        public DataModelBuilder dataField(DataField dataField) {
            this.dataModel.addDataField(dataField);
            return this;
        }

        public DataModel build() {
            return this.dataModel;
        }
    }
}
