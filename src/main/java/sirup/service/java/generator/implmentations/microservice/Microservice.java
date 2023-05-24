package sirup.service.java.generator.implmentations.microservice;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.api.RequestParser;
import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.Maven;
import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
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
import sirup.service.java.generator.implmentations.service.AbstractService;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;
import sirup.service.java.generator.interfaces.buildtool.IBuildTool;
import sirup.service.java.generator.interfaces.common.DockerService;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.database.IDatabase;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;
import sirup.service.log.rpc.client.LogClient;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static sirup.service.java.generator.implmentations.common.Type.*;
import static sirup.service.java.generator.implmentations.common.StringUtil.*;
import static sirup.service.log.rpc.client.ColorUtil.id;

public final class Microservice extends AbstractGenerateable {

    private final LogClient logger = LogClient.getInstance();

    private IApi api;
    private IBuildTool buildTool;
    private IDatabase database;
    private String name;
    private String id;
    private String packageName;
    private String groupId;
    private final Context context;
    private final List<AbstractInterface> interfaces;
    private Generateable dbInit;
    private final Generateable main;
    private Generateable dockerCompose;
    private boolean generateDocker;

    private List<Generateable> classes;
    private List<Generateable> otherFile;

    private Microservice() {
        //Default configuration
        this.api = Rest.DEFAULT;
        this.buildTool = Maven.DEFAULT;
        this.database = PostgreSQL.DEFAULT;
        this.name = "DEFAULT";
        this.packageName = ".microservice";
        this.groupId = "org.example";
        this.generateDocker = false;
        this.classes = new ArrayList<>();
        this.otherFile = new ArrayList<>();

        ServiceInterface serviceInterface = new ServiceInterface();
        ModelInterface modelInterface = new ModelInterface();
        this.main = new Main(this);
        this.context = new Context(this);
        this.interfaces = new ArrayList<>(){{
            add(serviceInterface);
            add(modelInterface);
        }};
    }

    public static Microservice fromJsonRequest(String jsonString) {
        return RequestParser.fromJsonRequest(jsonString);
    }
    public static Microservice fromJsonRequest(MicroserviceRequest microserviceRequest) {
        return RequestParser.fromJsonRequest(microserviceRequest);
    }

    public static MicroserviceBuilder builder() {
        return new MicroserviceBuilder();
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
        System.out.println(id);
    }
    private void setGenerateDocker(boolean generateDocker) {
        this.generateDocker = generateDocker;
    }
    public List<AbstractInterface> getInterfaces() {
        return this.interfaces;
    }

    public String make() {
        long start = System.currentTimeMillis();
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        FileGenerator fileGenerator = new FileGenerator(this.id, this.getName(), this.groupId);
        fileGenerator.generateFileStructure();
        for (Generateable clazz : this.classes) {
            fileGenerator.generateClassFile(clazz);
        }
        for (Generateable file : this.otherFile) {
            fileGenerator.generateClassFile(file);
        }
        logger.info("Microservice " + id(this.id) + " created in: " + (System.currentTimeMillis() - start) + "ms" );
        return this.id;
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(importGenerator -> {
                    importGenerator.generateImport(this.api.getImportString());
                    importGenerator.generateImport(this.database.getImportString());
                    importGenerator.generateImport(this.context.getImportString());
                    for (Generateable model: this.database.getDataModels()) {
                        importGenerator.generateImport(model.getImportString());
                    }
                    for (Generateable service : this.database.getServices()) {
                        importGenerator.generateImport(service.getImportString());
                    }
                })
                .classBody(classGenerator -> {
                    classGenerator.generateMethod("start", VOID.type, null, () -> {
                        classGenerator.write(tab(2) + "Context context = new Context();\n");
                        classGenerator.write(tab(2) + "context.addDatabase(new " + this.database.getName() + "());\n");
                        for (AbstractService service : this.database.getServices()) {
                            classGenerator.write(tab(2) + "context.addService(" + service.getDataModel().getName() + ".class, new " + service.getName() + "());\n");
                        }
                        classGenerator.write(tab(2) + "new " + this.api.getName() + "(context).start();\n");
                    });
                })
                .build()
                .make();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setGroupId(String groupId) {
        if (groupId != null){
            this.groupId = groupId;
        }
        this.packageName = this.groupId + ".microservice";
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public String getDir() {
        return StringUtil.SOURCE_DIR + "/" + this.getPackageName().replace(".", "/");
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
        public MicroserviceBuilder docker(boolean docker) {
            this.microservice.setGenerateDocker(docker);
            return this;
        }
        public MicroserviceBuilder groupId(String groupId) {
            this.microservice.setGroupId(groupId);
            return this;
        }
        public MicroserviceBuilder api(IApiBuilder<? extends IApi> apiBuilder) {
            return this.api(apiBuilder.build());
        }
        public MicroserviceBuilder api(IApi api) {
            this.microservice.setApi(api);
            return this;
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
            //TODO: simplify
            this.microservice.main.setGroupId(this.microservice.groupId);
            this.microservice.classes.add(this.microservice.main);
            this.microservice.buildTool.updateDependencies(this.microservice.api, this.microservice.database);
            this.microservice.buildTool.setGroupId(this.microservice.groupId);
            this.microservice.otherFile.add(this.microservice.buildTool);
            this.microservice.api.setGroupId(this.microservice.groupId);
            this.microservice.api.setContext(this.microservice.context);
            this.microservice.classes.add(this.microservice.api);
            this.microservice.database.setGroupId(this.microservice.groupId);
            this.microservice.classes.add(this.microservice.database);
            this.microservice.dbInit = this.microservice.database.getDbInit();
            this.microservice.otherFile.add(this.microservice.dbInit);
            for (Controller controller : this.microservice.api.getControllers()) {
                controller.setContext(this.microservice.context);
                controller.setGroupId(this.microservice.groupId);
                this.microservice.classes.add(controller);
            }
            for (AbstractService service : this.microservice.database.getServices()) {
                service.setGroupId(this.microservice.groupId);
                this.microservice.classes.add(service);
            }
            for (DataModel dataModel : this.microservice.database.getDataModels()) {
                dataModel.setGroupId(this.microservice.groupId);
                this.microservice.classes.add(dataModel);
            }
            this.microservice.context.setGroupId(this.microservice.groupId);
            this.microservice.classes.add(this.microservice.context);
            for (AbstractInterface generateable : this.microservice.interfaces) {
                generateable.setGroupId(this.microservice.groupId);
                generateable.setContext(this.microservice.context);
                this.microservice.classes.add(generateable);
            }
            if (this.microservice.generateDocker) {
                this.microservice.dockerCompose = new DockerComposeGenerator(
                        this.microservice.api.getDockerService(),
                        this.microservice.database.getDockerService());
                this.microservice.otherFile.add(this.microservice.dockerCompose);
            }
            return this.microservice;
        }
    }
    public static class Main extends AbstractGenerateable {

        private final Microservice microservice;

        public Main(final Microservice microservice) {
            this.microservice = microservice;
            this.packageName = "";
        }

        @Override
        public void fillFile(FileWriter fileWriter) throws IOException {
            ClassGenerator.builder()
                    .fileWriter(fileWriter)
                    .generateable(this)
                    .classType(ClassTypes.CLASS())
                    .classImports(importGenerator -> {
                        importGenerator.generateImport(this.microservice.getImportString());
                    })
                    .classBody(classGenerator -> {
                        classGenerator.generateMethod("main", VOID.type, new ArrayList<>(){{
                            add(new DataField(STRING_ARRAY.type,"args"));
                        }},() -> {
                            classGenerator.write(tab(2) + "new " + capitalize(this.microservice.getName()) + "().start();\n");
                        });
                    })
                    .build()
                    .make();
        }

        @Override
        public String getName() {
            return "Main";
        }
    }

}
