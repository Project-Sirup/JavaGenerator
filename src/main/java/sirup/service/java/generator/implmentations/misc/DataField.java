package sirup.service.java.generator.implmentations.misc;

public record DataField(Type type, String name) {

    public enum Type {
        STRING,
        INT32,
        INT64,
        FLOAT,
        BOOLEAN
    }
}
