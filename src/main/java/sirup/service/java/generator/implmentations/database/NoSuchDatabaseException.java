package sirup.service.java.generator.implmentations.database;

public class NoSuchDatabaseException extends RuntimeException {
    public NoSuchDatabaseException() {
        super();
    }
    public NoSuchDatabaseException(String message) {
        super(message);
    }
}
