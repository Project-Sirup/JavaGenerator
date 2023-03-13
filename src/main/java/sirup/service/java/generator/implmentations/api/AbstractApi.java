package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.Generateable;

public abstract class AbstractApi implements Generateable {

    protected String packageName;

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".api";
    }

    @Override
    public String packageName() {
        return this.packageName;
    }
}
