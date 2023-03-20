package sirup.service.java.generator.implmentations.common;

import sirup.service.java.generator.interfaces.common.Generateable;

public abstract class AbstractGenerateable implements Generateable {

    protected String packageName;

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public String getDir() {
        return StringUtil.SOURCE_DIR + "/" + this.getPackageName().replace(".", "/");
    }

    @Override
    public String getImportString() {
        return this.getPackageName() + "." + this.getName();
    }
}
