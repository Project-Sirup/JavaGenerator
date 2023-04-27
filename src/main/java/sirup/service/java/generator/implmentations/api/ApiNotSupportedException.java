package sirup.service.java.generator.implmentations.api;

public class ApiNotSupportedException extends RuntimeException {
    public ApiNotSupportedException() {
        super();
    }
    public ApiNotSupportedException(String message) {
        super(message);
    }
}
