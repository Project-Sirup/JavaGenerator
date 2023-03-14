package sirup.service.java.generator.implmentations.common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassGenerator {

    public static String packageString(String packageName) {
        return "package " + packageName + ";\n\n";
    }

    public static String importString(String importFullName) {
        return "import " + importFullName + ";\n";
    }
    public static String staticImportString(String staticImportFullName) {
        return "import static " + staticImportFullName + ";\n";
    }

    public static String classString(String className) {
        return "\npublic class " + StringUtil.capitalize(className) + " {\n\n";
    }
    public static String endClassSting() {
        return "}\n";
    }

    public static String methodString(String name, DataField.Type returnType, DataField[] args) {
        String argsString = args != null ? Arrays.stream(args).map(arg ->  arg.type().type + " " + arg.name()).collect(Collectors.joining(", ")) : "";
        return "\n\tpublic " + returnType.type + " " + name +
                "(" + argsString +
                ") {\n";
    }
    public static String staticMethodString(String name, DataField.Type returnType, DataField[] args) {
        String argsString = args != null ? Arrays.stream(args).map(arg ->  arg.type().type + " " + arg.name()).collect(Collectors.joining(", ")) : "";
        return "\n\tpublic static " + returnType.type + " " + name +
                "(" + argsString +
                ") {\n";
    }
    public static String endMethodString() {
        return "\t}\n";
    }

    public interface Filler {
        void fill() throws IOException;
    }
    public static void generateClass(FileWriter fileWriter, String className, Filler classContent) throws IOException {
        fileWriter.write(classString(className));
        classContent.fill();
        fileWriter.write(endClassSting());
    }
    public static void generateMethod(FileWriter fileWriter, String methodName, DataField.Type returnType, DataField[] args, Filler methodBody) throws IOException {
        fileWriter.write(methodString(methodName, returnType, args));
        methodBody.fill();
        fileWriter.write(endMethodString());
    }
    public static void generateStaticMethod(FileWriter fileWriter, String methodName, DataField.Type returnType, DataField[] args, Filler methodBody) throws IOException {
        fileWriter.write(staticMethodString(methodName, returnType, args));
        methodBody.fill();
        fileWriter.write(endMethodString());
    }
    public static void generateTryCatch(FileWriter fileWriter, Filler tryBlock, String exception, Filler catchBlock) throws IOException {
        fileWriter.write("\t\ttry {\n");
        tryBlock.fill();
        fileWriter.write("\t\t} catch (" + exception + " e) {\n");
        catchBlock.fill();
        fileWriter.write("\t\t}\n");
    }
    public static void generatePrivateAttribute(FileWriter fileWriter, String attributeName, DataField.Type type, String value) throws IOException {
        fileWriter.write("\tprivate " + type.type + " " + attributeName + " = " + value + ";\n");
    }
}
