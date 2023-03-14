package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.implmentations.common.StringUtil;
import sirup.service.java.generator.interfaces.common.Generateable;

public abstract class AbstractDatabase implements Generateable {

    protected String packageName;

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".database";
    }

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
