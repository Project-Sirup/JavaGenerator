package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MongoDB extends AbstractDatabase {

    private MongoDB() {}

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classBody(classGenerator -> {
                    classGenerator.write("\t\t//TODO: implement MongoDB\n");
                })
                .build()
                .make();
    }

    @Override
    public String getName() {
        return "MongoDB";
    }

    public static MongoDBBuilder builder() {
        return new MongoDBBuilder();
    }

    @Override
    public String getDependencyName() {
        return "MongoDB";
    }

    @Override
    public Generateable getDbInit() {
        return new MongoDB();
    }

    public static class MongoDBBuilder implements IDatabaseBuilder<MongoDB> {
        private final MongoDB mongoDB;
        private MongoDBBuilder() {
            this.mongoDB = new MongoDB();
        }

        @Override
        public IDatabaseBuilder<MongoDB> dataModels(List<DataModel> dataModels) {
            return this;
        }

        @Override
        public IDatabaseBuilder<MongoDB> options(MicroserviceRequest.Microservice.Database.Options options) {
            return this;
        }

        @Override
        public MongoDB build() {
            return this.mongoDB;
        }
    }

    public static class MongoInit {

    }
}
