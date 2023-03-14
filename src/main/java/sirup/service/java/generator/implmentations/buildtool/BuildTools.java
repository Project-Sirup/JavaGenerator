package sirup.service.java.generator.implmentations.buildtool;

public final class BuildTools {
    public static Maven.MavenBuilder mavenBuilder() {
        return Maven.builder();
    }
    public static Gradle.GradleBuilder gradleBuilder() {
        return Gradle.builder();
    }
}
