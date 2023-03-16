package sirup.service.java.generator.implmentations.model;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.StringUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;

public class DataModel extends AbstractGenerateable {
    private String name = "DEFAULT";
    private final List<DataField> dataFields;

    private DataModel() {
        this.dataFields = new ArrayList<>();
    }

    private void setName(String name) {
        this.name = name;
    }

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
        ClassGenerator classGenerator = new ClassGenerator(fileWriter, this);
        classGenerator.generateRecord(() -> {
            //imports
        }, this, () -> {
            //record body
        });
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

        public DataModelBuilder dataField(DataField.Type type, String name) {
            return this.dataField(new DataField(type, name));
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
