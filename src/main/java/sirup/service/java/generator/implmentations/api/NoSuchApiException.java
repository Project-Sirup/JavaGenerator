package sirup.service.java.generator.implmentations.api;

public class NoSuchApiException extends RuntimeException {
    public NoSuchApiException() {
        super();
    }
    public NoSuchApiException(String message) {
        super(message);
    }
}
