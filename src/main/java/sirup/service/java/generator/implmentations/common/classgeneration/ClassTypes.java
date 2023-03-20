package sirup.service.java.generator.implmentations.common.classgeneration;

public class ClassTypes {

    public static ClassType INTERFACE() {
        return ClassGenerator::generateInterface;
    }
    public static ClassType RECORD() {
        return ClassGenerator::generateRecord;
    }
    public static ClassType CLASS() {
        return ClassGenerator::generateClass;
    }
}