package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.interfaces.Generateable;

public abstract class AbstractDatabase implements Generateable {

    protected String packageName;

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".database";
    }

    @Override
    public String packageName() {
        return this.packageName;
    }
}
