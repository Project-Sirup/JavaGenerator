package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;

public abstract class AbstractBuildTool extends AbstractGenerateable {


    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getDir() {
        return "";
    }
}
