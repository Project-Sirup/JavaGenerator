package sirup.service.java.generator.implmentations.misc;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private String name = "DEFAULT";
    private final List<DataField> dataFields;

    private DataModel() {
        this.dataFields = new ArrayList<>();
    }

    private void setName(String name) {
        this.name = name;
    }

    private void addDataField(DataField dataField) {
        this.dataFields.add(dataField);
    }

    public static DataModelBuilder builder() {
        return new DataModelBuilder();
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
