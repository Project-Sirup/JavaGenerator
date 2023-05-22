package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.common.DockerService;
import sirup.service.java.generator.interfaces.common.Generateable;

import java.io.FileWriter;

public final class GraphQL extends AbstractApi {
    @Override
    public String getName() {
        return "GraphQL";
    }

    @Override
    public String getDependencyName() {
        return "GraphQL";
    }

    @Override
    public void fillFile(FileWriter fileWriter) {

    }

    @Override
    public Generateable getDockerfile() {
        return null;
    }

    @Override
    public DockerService getDockerService() {
        return null;
    }
}
