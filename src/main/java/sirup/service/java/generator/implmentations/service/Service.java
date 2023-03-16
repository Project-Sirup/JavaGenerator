package sirup.service.java.generator.implmentations.service;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.model.DataModel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;
import static sirup.service.java.generator.implmentations.common.DataField.Type.*;

public class Service extends AbstractGenerateable {

    private final DataModel dataModel;

    public Service(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public static Service of(DataModel dataModel) {
        return new Service(dataModel);
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator classGenerator = new ClassGenerator(fileWriter, this);
        classGenerator.generateClass(() -> {
            classGenerator.generateImport(this.dataModel.getImportString());
        }, () -> {
            DataField.Type t = CUSTOM;
            t.type = capitalize(this.dataModel.getName());
            String typeName = uncapitalize(this.dataModel.getName());
            List<DataField> typeArg = new ArrayList<>(){{
                add(new DataField(t, typeName));
            }};
            List<DataField> idArg = new ArrayList<>(){{
                add(new DataField(STRING, "id"));
            }};
            classGenerator.generateMethod("add", BOOLEAN, typeArg, () -> {
                fileWriter.write("\t\treturn false;\n");
            });
            classGenerator.generateMethod("get", t, idArg, () -> {
                fileWriter.write("\t\treturn null;\n");
            });
            classGenerator.generateMethod("put", BOOLEAN, typeArg, () -> {
                fileWriter.write("\t\treturn false;\n");
            });
            classGenerator.generateMethod("del", BOOLEAN, idArg, () -> {
                fileWriter.write("\t\treturn false;\n");
            });
        });
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".services";
    }

    @Override
    public String getName() {
        return capitalize(this.dataModel.getName()) + "Service";
    }
}
