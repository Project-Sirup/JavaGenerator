package sirup.service.java.generator.interfaces.common;

public interface Containerizable {
    Generateable getDockerfile();
    DockerService getDockerService();
}
