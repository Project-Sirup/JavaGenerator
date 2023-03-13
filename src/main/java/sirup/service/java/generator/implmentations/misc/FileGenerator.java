package sirup.service.java.generator.implmentations.misc;

import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.interfaces.Generateable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileGenerator {
    private static final String SOURCE_DIR = "src/main/java";
    private static final String RESOURCES_DIR = "src/main/resources";
    private static final String TEST_DIR = "src/test/java";

    private String outerName;
    private String basePackageName;
    private String basePackageDir;

    public FileGenerator(String outerName, String basePackageName) {
        this.outerName = outerName;
        this.basePackageName = basePackageName;
        this.basePackageDir = basePackageName.replace(".","/");
    }

    private String packageString(String packageName) {
        return "package " + packageName + ";\n\n";
    }

    private String classString(String className) {
        return "\tpublic class " + className + " {\n\n";
    }
    private String endClassSting() {
        return "}\n";
    }

    private String endMethodString() {
        return "\t}\n";
    }

    public void generate(Generateable generateable) {
        File file = new File(outerName + "/" + SOURCE_DIR + "/" + generateable.packageName().replace(".","/"));
        file.mkdirs();
        generateable.generate();
    }
    public void generateBase() {
        try {
            new File(outerName + "/" + SOURCE_DIR + "/" + basePackageDir).mkdirs();
            new File(outerName + "/" + RESOURCES_DIR).mkdirs();
            new File(outerName + "/" + TEST_DIR).mkdirs();

            File main = new File(outerName + "/" + SOURCE_DIR + "/" + basePackageDir + "/Main.java");
            main.createNewFile();
            try(FileWriter writer = new FileWriter(main)) {
                writer.write(packageString(this.basePackageName));
                writer.write(classString("Main"));
                writer.write("\tpublic static void main(String[] args) {\n");
                writer.write("\t\tSystem.out.println(\"hello world\");\n");
                writer.write(endMethodString());
                writer.write(endClassSting());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
