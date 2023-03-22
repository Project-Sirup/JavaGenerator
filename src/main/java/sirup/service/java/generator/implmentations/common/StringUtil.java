package sirup.service.java.generator.implmentations.common;

public class StringUtil {

    public static final String BASE_DIR = "generated";
    public static final String SOURCE_DIR = "src/main/java";
    public static final String RESOURCES_DIR = "src/main/resources";
    public static final String TEST_DIR = "src/test/java";

    public static String capitalize(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }
    public static String uncapitalize(String word) {
        return word.substring(0,1).toLowerCase() + word.substring(1);
    }

    public static String tab(int tabs) {
        return "\t".repeat(tabs);
    }
}
