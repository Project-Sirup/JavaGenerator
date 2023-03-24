package sirup.service.java.generator.implmentations.common;

import sirup.service.java.generator.interfaces.common.Generateable;

import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public abstract class AbstractGenerateable implements Generateable {

    protected String groupId;
    protected String packageName;

    @Override
    public void setGroupId(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            groupId = "org.example";
        }
        this.groupId = groupId;
        this.packageName = groupId + packageName;
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
        return this.getPackageName() + "." + capitalize(this.getName());
    }
}
