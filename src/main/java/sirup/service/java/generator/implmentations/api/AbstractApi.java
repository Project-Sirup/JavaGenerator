package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.interfaces.api.IApi;

import java.util.List;

public abstract class AbstractApi extends AbstractGenerateable implements IApi {

    protected List<Controller> controllers;

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".api";
    }

    @Override
    public List<Controller> getControllers() {
        return this.controllers;
    }
}
