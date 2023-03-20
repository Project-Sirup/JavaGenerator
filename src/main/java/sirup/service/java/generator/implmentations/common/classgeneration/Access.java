package sirup.service.java.generator.implmentations.common.classgeneration;

public enum Access {
    PRIVATE("private "),
    PROTECTED("protected "),
    PACKAGE(""),
    PUBLIC("public ");
    public final String string;
    Access(final String string) {
        this.string = string;
    }
}