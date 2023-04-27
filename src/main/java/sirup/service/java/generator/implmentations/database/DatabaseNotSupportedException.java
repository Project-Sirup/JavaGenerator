package sirup.service.java.generator.implmentations.database;

public class DatabaseNotSupportedException extends RuntimeException {
    public DatabaseNotSupportedException() {
        super();
    }
    public DatabaseNotSupportedException(String message) {
        super(message);
    }
}
