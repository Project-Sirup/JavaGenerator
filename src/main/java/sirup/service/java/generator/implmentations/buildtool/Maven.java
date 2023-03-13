package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.Dependency;
import sirup.service.java.generator.interfaces.IBuildTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Maven extends AbstractBuildTool implements IBuildTool {

    public static final Maven DEFAULT;

    private final List<Dependency> dependencies;

    static {
        DEFAULT = new Maven();
    }

    private Maven () {
        this.dependencies = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Maven";
    }

    @Override
    public void updateDependencies(Dependency ...dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
    }

    public static MavenBuilder builder() {
        return new MavenBuilder();
    }

    @Override
    public String toString() {
        return "maven: {\n" +
                "dependencies: {\n" +
                this.dependencies.stream().map(Dependency::getDependencyName).collect(Collectors.joining("\n")) + "\n" +
                "}";
    }

    @Override
    public void generate() {

    }

    public static class MavenBuilder {
        private final Maven maven;
        private MavenBuilder() {
            this.maven = new Maven();
        }

        public Maven build() {
            return this.maven;
        }
    }
}
