package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.buildtool.IBuildTool;
import sirup.service.java.generator.interfaces.buildtool.IBuildToolBuilder;

public final class BuildTools {
    public static Maven.MavenBuilder mavenBuilder() {
        return Maven.builder();
    }
    public static Gradle.GradleBuilder gradleBuilder() {
        return Gradle.builder();
    }
    public static IBuildToolBuilder<? extends IBuildTool> ofType(String buildTool){
        buildTool = buildTool.toLowerCase();
        switch (buildTool) {
            case "maven" -> {
                return Maven.builder();
            }
            case "gradle" -> {
                return Gradle.builder();
            }
            default -> throw new NoSuchBuildToolException("BuildTool [" + buildTool + "] is not supported");
        }
    }
}
