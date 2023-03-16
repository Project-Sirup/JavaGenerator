package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.api.IApi;

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
}
