package sirup.service.java.generator.implmentations.common;

public record DataField(Type type, String name) {

    public enum Type {
        STRING("String"),
        STRING_ARRAY("String[]"),
        INT32("int"),
        INT64("long"),
        FLOAT("float"),
        BOOLEAN("boolean"),
        VOID("void"),
        REQUEST("Request"),
        RESPONSE("Response"),
        CONNECTION("Connection");
        public final String type;
        Type(String type) {
            this.type = type;
        }
    }
}
