package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;

public abstract class AbstractBuildTool extends AbstractGenerateable {

    public AbstractBuildTool() {
        this.packageName = "";
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public String getDir() {
        return "";
    }
}
