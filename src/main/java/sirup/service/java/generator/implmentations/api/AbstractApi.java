package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.common.Generateable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractApi extends AbstractGenerateable implements IApi {

    protected List<Controller> controllers;
    protected int port;
    protected Generateable context;

    public AbstractApi() {
        this.controllers = new ArrayList<>();
        this.packageName = ".api";
    }

    @Override
    public void setContext(Generateable context) {
        this.context = context;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public List<Controller> getControllers() {
        return this.controllers;
    }
}
