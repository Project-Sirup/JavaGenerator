package sirup.service.java.generator.implmentations;

import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.Maven;
import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassType;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.context.Context;
import sirup.service.java.generator.implmentations.interfaces.AbstractInterface;
import sirup.service.java.generator.implmentations.interfaces.ModelInterface;
import sirup.service.java.generator.implmentations.interfaces.ServiceInterface;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.implmentations.database.PostgreSQL;
import sirup.service.java.generator.implmentations.common.FileGenerator;
import sirup.service.java.generator.implmentations.common.StringUtil;
import sirup.service.java.generator.implmentations.service.Service;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;
import sirup.service.java.generator.interfaces.buildtool.IBuildTool;
import sirup.service.java.generator.interfaces.buildtool.IBuildToolBuilder;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.database.IDatabase;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.Type.*;

public final class Microservice extends AbstractGenerateable {

    private IApi api;
    private IBuildTool buildTool;
    private IDatabase database;
    private String name;
    private String id;
    private String packageName;
    private final Context context;
    private final List<AbstractInterface> interfaces;
    private Generateable dbInit;

    private Microservice() {
        //Default configuration
        this.api = Rest.DEFAULT;
        this.buildTool = Maven.DEFAULT;
        this.database = PostgreSQL.DEFAULT;
        this.name = "DEFAULT";
        this.packageName = "org.example";

        ServiceInterface serviceInterface = new ServiceInterface();
        ModelInterface modelInterface = new ModelInterface();

        this.context = new Context(this);
        this.interfaces = new ArrayList<>(){{
            add(serviceInterface);
            add(modelInterface);
        }};
    }

    public static MicroserviceBuilder builder() {
        return new MicroserviceBuilder();
    }

    @Override
    public String toString() {
        return "{\n" +
                "api: {\n" +
                api.toString() + "\n" +
                "}\n" +
                "buildTool: {\n" +
                buildTool.toString() + "\n" +
                "}\n" +
                "database: {\n" +
                database.toString() + "\n" +
                "}\n" +
                "}";
    }

    private void setApi(IApi api) {
        this.api = api;
    }
    private void setBuildTool(IBuildTool buildTool) {
        this.buildTool = buildTool;
    }
    private void setDatabase(IDatabase database) {
        this.database = database;
    }
    public IDatabase getDatabase() {
        return this.database;
    }
    private void setName(String name) {
        this.name = name;
    }
    private void setId(String id) {
        this.id = id;
    }
    public List<AbstractInterface> getInterfaces() {
        return this.interfaces;
    }

    public void make() {
        FileGenerator fileGenerator = new FileGenerator(this.id, this.getName(), this.getPackageName());
        fileGenerator.generateFileStructure();
        fileGenerator.generateClassFile(this);
        fileGenerator.generateClassFile(this.api);
        fileGenerator.generateClassFile(this.database);
        for (Controller controller : this.api.getControllers()) {
            fileGenerator.generateClassFile(controller);
        }
        for (Service service : this.database.getServices()) {
            fileGenerator.generateClassFile(service);
        }
        for (DataModel dataModel : this.database.getDataModels()) {
            fileGenerator.generateClassFile(dataModel);
        }
        fileGenerator.generateClassFile(this.context);
        for (Generateable generateable : this.interfaces) {
            fileGenerator.generateClassFile(generateable);
        }
        fileGenerator.generate(this.dbInit);
        fileGenerator.generate(this.buildTool);
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(classGenerator -> {
                    classGenerator.generateImport(this.api.getImportString());
                    classGenerator.generateImport(this.database.getImportString());
                    classGenerator.generateImport(this.context.getImportString());
                    for (Generateable model: this.database.getDataModels()) {
                        classGenerator.generateImport(model.getImportString());
                    }
                    for (Generateable service : this.database.getServices()) {
                        classGenerator.generateImport(service.getImportString());
                    }
                })
                .classBody(classGenerator -> {
                    classGenerator.generateStaticMethod("main", VOID.type, new DataField[]{new DataField(STRING_ARRAY.type, "args")}, () -> {
                        classGenerator.write("\t\tContext context = new Context();\n");
                        classGenerator.write("\t\tcontext.addDatabase(new " + this.database.getName() + "());\n");
                        for (Service service : this.database.getServices()) {
                            classGenerator.write("\t\tcontext.addService(" + service.getDataModel().getName() + ".class, new " + service.getName() + "());\n");
                        }
                        classGenerator.write("\t\tnew " + this.api.getName() + "(context).start();\n");
                    });
                })
                .build()
                .make();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public String getDir() {
        return StringUtil.SOURCE_DIR + "/" + this.getPackageName().replace(".", "/");
    }

    @Override
    public String getImportString() {
        return this.getPackageName() + "." + this.getName();
    }

    public static class MicroserviceBuilder {
        private final Microservice microservice;
        private MicroserviceBuilder() {
            this.microservice = new Microservice();
        }

        public MicroserviceBuilder id(String id) {
            this.microservice.setId(id);
            return this;
        }
        public MicroserviceBuilder name(String name) {
            this.microservice.setName(name);
            return this;
        }
        public MicroserviceBuilder packageName(String packageName) {
            this.microservice.setPackageName(packageName);
            return this;
        }
        public MicroserviceBuilder api(IApiBuilder<? extends IApi> apiBuilder) {
            return this.api(apiBuilder.build());
        }
        public MicroserviceBuilder api(IApi api) {
            this.microservice.setApi(api);
            return this;
        }
        public MicroserviceBuilder buildTool(IBuildToolBuilder<? extends IBuildTool> buildToolBuilder) {
            return this.buildTool(buildToolBuilder.build());
        }
        public MicroserviceBuilder buildTool(IBuildTool buildTool) {
            this.microservice.setBuildTool(buildTool);
            return this;
        }
        public MicroserviceBuilder database(IDatabaseBuilder<? extends IDatabase> databaseBuilder) {
            return this.database(databaseBuilder.build());
        }
        public MicroserviceBuilder database(IDatabase database) {
            this.microservice.setDatabase(database);
            return this;
        }

        public Microservice build() {
            this.microservice.buildTool.updateDependencies(this.microservice.api, this.microservice.database);
            this.microservice.api.setPackageName(this.microservice.getPackageName());
            this.microservice.api.setContext(this.microservice.context);
            this.microservice.database.setPackageName(this.microservice.getPackageName());
            this.microservice.dbInit = this.microservice.database.getDbInit();
            for (Controller controller : this.microservice.api.getControllers()) {
                controller.setContext(this.microservice.context);
                controller.setPackageName(this.microservice.getPackageName());
            }
            for (Service service : this.microservice.database.getServices()) {
                service.setPackageName(this.microservice.getPackageName());
                service.setContext(this.microservice.context);
            }
            for (DataModel dataModel : this.microservice.database.getDataModels()) {
                dataModel.setPackageName(this.microservice.getPackageName());
            }
            this.microservice.context.setPackageName(this.microservice.getPackageName());
            for (AbstractInterface generateable : this.microservice.interfaces) {
                generateable.setPackageName(this.microservice.getPackageName());
                generateable.setContext(this.microservice.context);
            }
            return this.microservice;
        }
    }
}
