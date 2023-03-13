package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.Generateable;

public abstract class AbstractBuildTool implements Generateable {

    protected String packageName;

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String packageName() {
        return "";
    }
}
