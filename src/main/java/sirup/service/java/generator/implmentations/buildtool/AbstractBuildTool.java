package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.common.Generateable;

public abstract class AbstractBuildTool implements Generateable {

    protected String packageName;

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public String getDir() {
        return "";
    }

    @Override
    public String getImportString() {
        return this.getPackageName() + "." + this.getName();
    }
}
