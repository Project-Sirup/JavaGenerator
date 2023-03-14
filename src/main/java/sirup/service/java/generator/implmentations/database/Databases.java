package sirup.service.java.generator.implmentations.database;

public final class Databases {
    public static PostgreSQL.PostgreSQLBuilder postgreSQLBuilder() {
        return PostgreSQL.builder();
    }
}
