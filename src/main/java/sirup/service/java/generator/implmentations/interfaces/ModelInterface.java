package sirup.service.java.generator.implmentations.interfaces;

import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;

import java.io.FileWriter;
import java.io.IOException;

public class ModelInterface extends AbstractInterface {

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.INTERFACE())
                .build()
                .make();
    }

    @Override
    public String getName() {
        return "Model";
    }
}
