package sirup.service.java.generator.implmentations.microservice;

public class LanguageNotSupportedException extends RuntimeException {
    public LanguageNotSupportedException() {
        super();
    }
    public LanguageNotSupportedException(String message) {
        super(message);
    }
}
