package sirup.service.java.generator.interfaces.api;

import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.common.Nameable;

import java.util.List;

public interface IApi extends Nameable, Dependency, Generateable {
    List<Controller> getControllers();
    int getPort();
    void setContext(Generateable context);
}
