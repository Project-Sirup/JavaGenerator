package sirup.service.java.generator.implmentations.common;

public record DataField(String type, String name, String ref) {
    public DataField(String type, String name) {
        this(type, name, null);
    }
}
