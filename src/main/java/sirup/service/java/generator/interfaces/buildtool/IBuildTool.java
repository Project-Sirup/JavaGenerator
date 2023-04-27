package sirup.service.java.generator.interfaces.buildtool;

import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.common.Generateable;

public interface IBuildTool extends Generateable {
    void updateDependencies(Dependency...dependencies);
}
