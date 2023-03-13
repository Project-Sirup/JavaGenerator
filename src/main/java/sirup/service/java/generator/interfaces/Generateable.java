package sirup.service.java.generator.interfaces;

public interface Generateable extends Nameable {
    void setPackageName(String packageName);
    String packageName();
    void generate();
}
