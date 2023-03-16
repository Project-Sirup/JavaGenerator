package sirup.service.java.generator.implmentations.controller;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.common.StringUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static sirup.service.java.generator.implmentations.common.ClassGenerator.*;
import static sirup.service.java.generator.implmentations.common.DataField.Type.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public class Controller extends AbstractGenerateable {

    private final DataModel dataModel;
    private final Set<String> methods;

    private Controller(DataModel dataModel) {
        this.dataModel = dataModel;
        this.methods = new HashSet<>();
    }

    public static Controller of(DataModel dataModel) {
        return new Controller(dataModel);
    }

    public String addMethod(String method) {
        int i = 1;
        String originalName = method + capitalize(this.dataModel.getName());
        String updatedName = originalName;
        while (this.methods.contains(updatedName)) {
            System.out.println(updatedName);
            updatedName = originalName + i++;
        }
        this.methods.add(updatedName);
        return updatedName;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".controllers";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator classGenerator = new ClassGenerator(fileWriter, this);
        classGenerator.generateClass(() -> {
            classGenerator.generateImport("spark.Request");
            classGenerator.generateImport("spark.Response");
        }, () -> {
            List<DataField> args = new ArrayList<>(){{
                add(new DataField(REQUEST, "request"));
                add(new DataField(RESPONSE, "response"));
            }};

            for (String method : methods) {
                classGenerator.generateMethod(method, OBJECT, args, () -> {
                    fileWriter.write("\t\treturn \"not yet implemented\";\n");
                });
            }
        });
    }

    @Override
    public String getName() {
        return StringUtil.capitalize(this.dataModel.getName()) + "Controller";
    }
}
