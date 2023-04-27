package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.buildtool.IBuildTool;

public final class BuildTools {
    public static IBuildTool ofType(String buildTool){
        buildTool = buildTool.toLowerCase();
        switch (buildTool) {
            case "maven" -> {
                return new Maven();
            }
            case "gradle" -> {
                return new Gradle();
            }
            default -> throw new NoSuchBuildToolException("BuildTool [" + buildTool + "] is not supported");
        }
    }
}
