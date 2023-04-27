package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.buildtool.IBuildTool;

import java.io.FileWriter;

public final class Gradle extends AbstractBuildTool implements IBuildTool {
    @Override
    public void updateDependencies(Dependency... dependencies) {

    }

    Gradle() {}

    @Override
    public String getName() {
        return "Gradle";
    }

    @Override
    public void fillFile(FileWriter fileWriter) {

    }
}
