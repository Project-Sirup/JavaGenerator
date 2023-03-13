package sirup.service.java.generator.implmentations;

import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.Maven;
import sirup.service.java.generator.implmentations.database.PostgreSQL;
import sirup.service.java.generator.implmentations.misc.FileGenerator;
import sirup.service.java.generator.interfaces.*;

public final class Microservice implements Generateable {

    private IApi api;
    private IBuildTool buildTool;
    private IDatabase database;
    private String name;
    private String packageName;

    private Microservice() {
        //Default configuration
        this.api = Rest.DEFAULT;
        this.buildTool = Maven.DEFAULT;
        this.database = PostgreSQL.DEFAULT;
        this.name = "DEFAULT";
        this.packageName = "org.example";
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
    private void setName(String name) {
        this.name = name;
    }

    public void make() {
        FileGenerator fileGenerator = new FileGenerator(this.getName(), this.packageName());
        fileGenerator.generateBase();
        fileGenerator.generate(this.api);
        fileGenerator.generate(this.database);
    }

    @Override
    public void generate() {

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
    public String packageName() {
        return this.packageName;
    }

    public static class MicroserviceBuilder {
        private final Microservice microservice;
        private MicroserviceBuilder() {
            this.microservice = new Microservice();
        }

        public MicroserviceBuilder name(String name) {
            this.microservice.setName(name);
            return this;
        }
        public MicroserviceBuilder packageName(String packageName) {
            this.microservice.setPackageName(packageName);
            return this;
        }
        public MicroserviceBuilder api(IApi api) {
            this.microservice.setApi(api);
            return this;
        }
        public MicroserviceBuilder buildTool(IBuildTool buildTool) {
            this.microservice.setBuildTool(buildTool);
            return this;
        }
        public MicroserviceBuilder database(IDatabase database) {
            this.microservice.setDatabase(database);
            return this;
        }

        public Microservice build() {
            this.microservice.buildTool.updateDependencies(this.microservice.api, this.microservice.database);
            this.microservice.api.setPackageName(this.microservice.packageName());
            this.microservice.database.setPackageName(this.microservice.packageName());
            return this.microservice;
        }
    }
}
