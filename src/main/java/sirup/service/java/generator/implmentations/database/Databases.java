package sirup.service.java.generator.implmentations.database;

import sirup.service.java.generator.interfaces.database.IDatabase;
import sirup.service.java.generator.interfaces.database.IDatabaseBuilder;

public final class Databases {
    public static PostgreSQL.PostgreSQLBuilder postgreSQLBuilder() {
        return PostgreSQL.builder();
    }
    public static IDatabaseBuilder<? extends IDatabase> ofType(String database) {
        database = database.toLowerCase();
        switch (database) {
            case "postgresql", "postgres" -> {
                return PostgreSQL.builder();
            }
            case "mongo", "mongodb" -> {
                return MongoDB.builder();
            }
            default -> throw new DatabaseNotSupportedException("Database [" + database + "] is not supported");
        }
    }
}
