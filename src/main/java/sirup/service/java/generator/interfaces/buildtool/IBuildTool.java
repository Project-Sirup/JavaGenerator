package sirup.service.java.generator.interfaces.buildtool;

import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.common.Generateable;
import sirup.service.java.generator.interfaces.common.Nameable;

public interface IBuildTool extends Nameable, Generateable {
    void updateDependencies(Dependency...dependencies);
}
