package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.Dependency;
import sirup.service.java.generator.interfaces.IBuildTool;

public final class Gradle extends AbstractBuildTool implements IBuildTool {
    @Override
    public void updateDependencies(Dependency... dependencies) {

    }

    @Override
    public String getName() {
        return "Gradle";
    }

    @Override
    public void generate() {

    }
}
