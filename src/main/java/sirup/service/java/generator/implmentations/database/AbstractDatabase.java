package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;

public abstract class AbstractDatabase extends AbstractGenerateable {

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".database";
    }
}
