package sirup.service.java.generator.interfaces;

public interface IBuildTool extends Nameable, Generateable {
    void updateDependencies(Dependency ...dependencies);
}
