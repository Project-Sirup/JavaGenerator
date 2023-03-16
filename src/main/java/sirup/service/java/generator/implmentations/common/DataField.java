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
        OBJECT("Object"),
        REQUEST("Request"),
        RESPONSE("Response"),
        CONNECTION("Connection"),
        CUSTOM("");
        public String type;
        Type(String type) {
            this.type = type;
        }
        public static Type custom(String customType) {
            CUSTOM.type = StringUtil.capitalize(customType);
            return CUSTOM;
        }
    }
}
