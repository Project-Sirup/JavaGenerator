package sirup.service.java.generator.interfaces.common;

public interface DockerService {
    String getName();
    int getInternalPort();
    int getExternalPort();
    String getBuildContext();
}
