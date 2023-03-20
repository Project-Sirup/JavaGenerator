package sirup.service.java.generator.implmentations.interfaces;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.context.Context;
import sirup.service.java.generator.interfaces.common.Contextable;

public abstract class AbstractInterface extends AbstractGenerateable implements Contextable {

    protected Context context;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".interfaces";
    }
}
