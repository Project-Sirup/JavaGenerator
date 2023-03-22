package sirup.service.java.generator.implmentations.controller;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.classgeneration.Access;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.context.Context;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.common.StringUtil;
import sirup.service.java.generator.interfaces.common.Contextable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator.*;
import static sirup.service.java.generator.implmentations.common.Type.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public class Controller extends AbstractGenerateable implements Contextable {

    private final DataModel dataModel;
    private final Set<String> methods;
    private final Map<String, String> methodLink;
    private Context context;

    private Controller(DataModel dataModel) {
        this.dataModel = dataModel;
        this.methods = new HashSet<>();
        this.methodLink = new HashMap<>();
    }

    public static Controller of(DataModel dataModel) {
        return new Controller(dataModel);
    }

    public String addMethod(String method, String link) {
        int i = 1;
        String originalName = method + this.dataModel.getName();
        String updatedName = originalName;
        while (this.methods.contains(updatedName)) {
            System.out.println(updatedName);
            updatedName = originalName + i++;
        }
        this.methods.add(updatedName);
        this.methodLink.put(updatedName, link);
        return updatedName;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".controllers";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(classGenerator -> {
                    classGenerator.generateImport("com.google.gson.Gson");
                    classGenerator.generateImport("spark.Request");
                    classGenerator.generateImport("spark.Response");
                    classGenerator.generateImport(this.context.getImportString());
                    classGenerator.generateImport(this.dataModel.getImportString());
                    String[] splitString = this.getImportString().split("\\.");
                    splitString[splitString.length -2] = "services";
                    splitString[splitString.length -1] = this.dataModel.getName() + "Service";
                    String serviceImportString = String.join(".", splitString);
                    classGenerator.generateImport(serviceImportString);
                })
                .classBody(classGenerator -> {
                    classGenerator.generateAttribute(Access.PRIVATE, "context","Context", "null");
                    classGenerator.generateAttribute(Access.PRIVATE, "service", this.dataModel.getName() + "Service", "null");
                    classGenerator.generateAttribute(Access.PRIVATE, "gson", "Gson", "new Gson()");
                    classGenerator.generateConstructor(new ArrayList<>(){{
                        add(new DataField("Context", "context"));
                    }}, () -> {
                        classGenerator.write(tab(2) + "this.context = context;\n");
                        classGenerator.write(tab(2) + "this.service = (" + this.dataModel.getName() +
                                "Service) context.getService(" + this.dataModel.getName() + ".class);\n");
                    });
                    List<DataField> args = new ArrayList<>(){{
                        add(new DataField(REQUEST.type, "request"));
                        add(new DataField(RESPONSE.type, "response"));
                    }};

                    for (String method : methods) {
                        classGenerator.generateMethod(method, OBJECT.type, args, () -> {
                            classGenerator.write(translateMethod(method) + ";\n");
                        });
                    }
                    classGenerator.write(tab(1) + "private record " + this.dataModel.getName() + "Request(" +
                            this.dataModel.getName() + " " + uncapitalize(this.dataModel.getName()) + ") {}\n");
                })
                .build()
                .make();
    }

    private String translateMethod(String inputMethod) {
        inputMethod = inputMethod.toLowerCase();
        if (inputMethod.contains("store") || inputMethod.contains("create") || inputMethod.contains("add") || inputMethod.contains("new")) {
            return tab(2) + this.dataModel.getName() + "Request "
                    + uncapitalize(this.dataModel.getName()) + "Request = this.gson.fromJson(request.body(), " + this.dataModel.getName() + "Request.class);\n" +
                    tab(2) + "return this.service.add(" + uncapitalize(this.dataModel.getName()) + "Request." + uncapitalize(this.dataModel.getName()) + "())";
        }
        if (inputMethod.contains("findall") || inputMethod.contains("getall")) {
            return tab(2) + "return this.service.getAll()";
        }
        if (inputMethod.contains("find") || inputMethod.contains("get")) {
            return tab(2) + "return this.service.get(request.params(\"" + this.dataModel.getDataFields().get(0).name() + "\"))";
        }
        if (inputMethod.contains("change") || inputMethod.contains("update") || inputMethod.contains("put") || inputMethod.contains("patch")) {
            return tab(2) + this.dataModel.getName() + "Request "
                    + uncapitalize(this.dataModel.getName()) + "Request = this.gson.fromJson(request.body(), " + this.dataModel.getName() + "Request.class);\n" +
                    tab(2) + "return this.service.update(" + uncapitalize(this.dataModel.getName()) + "Request." + uncapitalize(this.dataModel.getName()) + "())";
        }
        if (inputMethod.contains("delete") || inputMethod.contains("remove") || inputMethod.contains("destroy")) {
            return tab(2) + "return this.service.remove(request.params(\"" + this.dataModel.getDataFields().get(0).name() + "\"))";
        }
        throw new IllegalArgumentException("cannot translate " + inputMethod);
    }

    @Override
    public String getName() {
        return StringUtil.capitalize(this.dataModel.getName()) + "Controller";
    }
}
