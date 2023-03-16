package sirup.service.java.generator.implmentations.common;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import sirup.service.java.generator.implmentations.common.DataField.Type;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Generateable;

public final class ClassGenerator {

    private final FileWriter fileWriter;
    private final Generateable generateable;

    public ClassGenerator(final FileWriter fileWriter, final Generateable generateable) {
        this.fileWriter = fileWriter;
        this.generateable = generateable;
    }

    private String packageString(String packageName) {
        return "package " + packageName + ";\n\n";
    }

    private String importString(String fullImportName) {
        return "import " + fullImportName + ";\n";
    }
    private String staticImportString(String staticImportFullName) {
        return "import static " + staticImportFullName + ";\n";
    }

    private String classString(String className) {
        return "\npublic class " + StringUtil.capitalize(className) + " {\n\n";
    }
    private String recordString(String recordName, List<DataField> fields) {
        return "\npublic record " + StringUtil.capitalize(recordName) +
                "(" + fields.stream().map(field ->  field.type().type + " " + field.name()).collect(Collectors.joining(", "))  + ") {\n\n";
    }
    private String endClassSting() {
        return "}\n";
    }

    private String methodString(String name, Type returnType, List<DataField> args) {
        String argsString = args != null ? args.stream().map(arg ->  arg.type().type + " " + arg.name()).collect(Collectors.joining(", ")) : "";
        return "\n\tpublic " + returnType.type + " " + name +
                "(" + argsString +
                ") {\n";
    }
    private String staticMethodString(String name, Type returnType, DataField[] args) {
        String argsString = args != null ? Arrays.stream(args).map(arg ->  arg.type().type + " " + arg.name()).collect(Collectors.joining(", ")) : "";
        return "\n\tpublic static " + returnType.type + " " + name +
                "(" + argsString +
                ") {\n";
    }
    private String endMethodString() {
        return "\t}\n";
    }

    public interface Filler {
        void fill() throws IOException;
    }
    public void generateClass(Filler imports, Filler classContent) throws IOException {
        this.fileWriter.write(packageString(generateable.getPackageName()));
        imports.fill();
        this.fileWriter.write(classString(this.generateable.getName()));
        classContent.fill();
        this.fileWriter.write(endClassSting());
    }
    public void generateRecord(Filler imports, DataModel dataModel, Filler recordContent) throws IOException {
        this.fileWriter.write(packageString(this.generateable.getPackageName()));
        imports.fill();
        this.fileWriter.write(recordString(dataModel.getName(), dataModel.getDataFields()));
        recordContent.fill();
        this.fileWriter.write(endClassSting());
    }
    public void generateImport(String fullImportName) throws IOException {
        this.fileWriter.write(importString(fullImportName));
    }
    public void generateStaticImport(String fullImportName) throws IOException {
        this.fileWriter.write(staticImportString(fullImportName));
    }
    public void generateMethod(String methodName, Type returnType, List<DataField> args, Filler methodBody) throws IOException {
        this.fileWriter.write(methodString(methodName, returnType, args));
        methodBody.fill();
        this.fileWriter.write(endMethodString());
    }
    public void generateStaticMethod(String methodName, Type returnType, DataField[] args, Filler methodBody) throws IOException {
        this.fileWriter.write(staticMethodString(methodName, returnType, args));
        methodBody.fill();
        this.fileWriter.write(endMethodString());
    }
    public void generateTryCatch(Filler tryBlock, String exception, Filler catchBlock) throws IOException {
        this.fileWriter.write("\t\ttry {\n");
        tryBlock.fill();
        this.fileWriter.write("\t\t} catch (" + exception + " e) {\n");
        catchBlock.fill();
        this.fileWriter.write("\t\t}\n");
    }
    public void generatePrivateAttribute(String attributeName, Type type, String value) throws IOException {
        this.fileWriter.write("\tprivate " + type.type + " " + attributeName + " = " + value + ";\n");
    }
    public void generateMethodVar(String varName, Type type, String value) throws IOException {
        this.fileWriter.write("\t\t" + type.type + " " + varName + " = " + value + ";\n");
    };
}
