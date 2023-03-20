package sirup.service.java.generator.implmentations.buildtool;

public class NoSuchBuildToolException extends RuntimeException {
    public NoSuchBuildToolException() {
        super();
    }
    public NoSuchBuildToolException(String message) {
        super(message);
    }
}
