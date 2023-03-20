package sirup.service.java.generator.implmentations.common;

public enum Type {
    STRING("String"),
    STRING_ARRAY("String[]"),
    INT32("int"),
    INT64("long"),
    FLOAT("float"),
    BOOLEAN("boolean"),
    VOID("void"),
    OBJECT("Object"),
    REQUEST("Request"),
    RESPONSE("Response"),
    CONNECTION("Connection");
    public final String type;
    Type(final String type) {
        this.type = type;
    }
}