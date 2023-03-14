package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.buildtool.IBuildTool;
import sirup.service.java.generator.interfaces.buildtool.IBuildToolBuilder;

import java.io.FileWriter;

public final class Gradle extends AbstractBuildTool implements IBuildTool {
    @Override
    public void updateDependencies(Dependency... dependencies) {

    }

    @Override
    public String getName() {
        return "Gradle";
    }

    @Override
    public void fillFile(FileWriter fileWriter) {

    }

    static GradleBuilder builder() {
        return new GradleBuilder();
    }

    public static class GradleBuilder implements IBuildToolBuilder<Gradle> {
        private final Gradle gradle;
        private GradleBuilder() {
            this.gradle = new Gradle();
        }

        @Override
        public Gradle build() {
            return this.gradle;
        }
    }
}
