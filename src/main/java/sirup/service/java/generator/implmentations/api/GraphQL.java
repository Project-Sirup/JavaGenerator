package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.IApi;

public final class GraphQL extends AbstractApi implements IApi {
    @Override
    public String getName() {
        return "GraphQL";
    }

    @Override
    public String getDependencyName() {
        return "GraphQL";
    }

    @Override
    public void generate() {

    }
}
