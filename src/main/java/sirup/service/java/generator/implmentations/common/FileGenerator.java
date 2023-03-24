package sirup.service.java.generator.implmentations.common;

import sirup.service.java.generator.interfaces.common.Generateable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public class FileGenerator {

    private String outerName;
    private String microserviceId;
    private String basePackageName;
    private String basePackageDir;

    public FileGenerator(String microserviceId, String outerName, String basePackageName) {
        this.microserviceId = microserviceId;
        this.outerName = outerName;
        this.basePackageName = basePackageName;
        this.basePackageDir = basePackageName.replace(".","/");
    }

    public void generateClassFile(Generateable generateable) {
        try {
            String filePath =
                    BASE_DIR + "/" + microserviceId + "/" + outerName + "/" +
                    generateable.getDir() + "/" +
                    capitalize(generateable.getName()) + ".java";
            File file = new File(filePath);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            generateable.fillFile(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void generate(Generateable generateable) {
        try {
            File file = new File(
                    BASE_DIR + "/" + microserviceId + "/" + outerName + "/" +
                            generateable.getDir() + "/" +
                            generateable.getName());
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            generateable.fillFile(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void generateFileStructure() {
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir).mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/api").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/context").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/controllers").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/database").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/interfaces").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/microservice").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/models").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/services").mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + RESOURCES_DIR).mkdirs();
        new File(BASE_DIR + "/" + microserviceId + "/" + outerName + "/" + TEST_DIR).mkdirs();
    }
}
