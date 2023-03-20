package sirup.service.java.generator.implmentations.common.classgeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.model.DataModel;
import sirup.service.java.generator.interfaces.common.Builder;
import sirup.service.java.generator.interfaces.common.Generateable;

import static sirup.service.java.generator.implmentations.common.StringUtil.*;

public final class ClassGenerator {

    private FileWriter fileWriter;
    private Generateable generateable;
    private Filler importsFiller = (classGenerator) -> {};
    private Filler classBodyFiller = (classGenerator) -> {};
    private ClassType classType = ClassTypes.CLASS();
    private String generic = null;
    private String superClass = null;
    private List<String> implementations = null;
    private DataModel dataModel = null;

    private ClassGenerator() {}

    private void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }
    private void setGenerateable(Generateable generateable) {
        this.generateable = generateable;
    }
    private void setImportsFiller(Filler importsFiller) {
        this.importsFiller = importsFiller;
    }
    private void setClassBodyFiller(Filler classBodyFiller) {
        this.classBodyFiller = classBodyFiller;
    }
    private void setClassType(ClassType classType) {
        this.classType = classType;
    }
    private void setGeneric(String generic) {
        this.generic = generic;
    }
    private void addImplementation(String implementation) {
        if (this.implementations == null) {
            this.implementations = new ArrayList<>();
        }
        this.implementations.add(implementation);
    }
    private void setSuperClass(String superClass) {
        this.superClass = superClass;
    }
    private void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public void make() throws IOException {
        this.classType.fill(this);
    }

    public void write(String string) throws IOException {
        this.fileWriter.write(string);
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

    private String classString() {
        String t = this.generic == null ? "" : "<" + this.generic + ">";
        String impl = this.implementations == null ? "" : " implements " + String.join(", ", this.implementations);
        String ext = this.superClass == null ? "" : " extends " + this.superClass;
        return "\npublic class " + capitalize(this.generateable.getName()) + t + ext + impl + " {\n";
    }
    private String recordString() {
        String impl = this.implementations == null ? "" : " implements " + String.join(", ", this.implementations);
        return "\npublic record " + capitalize(this.generateable.getName()) +
                "(" + this.dataModel.getDataFields().stream().map(field -> field.type() + " " + field.name()).collect(Collectors.joining(", "))  + ") " +
                impl + " {\n";
    }
    private String interfaceString() {
        String t = this.generic == null ? "" : "<" + this.generic + ">";
        String ext = this.implementations == null ? "" : " extends " + String.join(", ", this.implementations);
        return "\npublic interface " + capitalize(this.generateable.getName()) + t + ext + " {\n";
    }
    private String endClassSting() {
        return "}\n";
    }

    private String methodString(String name, String returnType, List<DataField> args) {
        String argsString = args != null ? args.stream().map(arg ->  arg.type() + " " + arg.name()).collect(Collectors.joining(", ")) : "";
        return "\tpublic " + returnType + " " + name +
                "(" + argsString + ") {\n";
    }
    private String staticMethodString(String name, String returnType, DataField[] args) {
        String argsString = args != null ? Arrays.stream(args).map(arg ->  arg.type() + " " + arg.name()).collect(Collectors.joining(", ")) : "";
        return "\n\tpublic static " + returnType + " " + name +
                "(" + argsString + ") {\n";
    }
    private String abstractMethodString(String methodName, String returnType, List<DataField> args) {
        String argsString = args == null ? "" : args.stream().map(arg -> arg.type() + " " + arg.name()).collect(Collectors.joining(", "));
        return "\t" + returnType + " " + methodName + "(" + argsString + ");\n";
    }
    private String endMethodString() {
        return "\t}\n";
    }

    public interface Filler {
        void fill(ClassGenerator classGenerator) throws IOException;
    }
    public interface InnerFiller {
        void fill() throws IOException;
    }

    private void generateJava(Filler filler) throws IOException {
        this.fileWriter.write(packageString(this.generateable.getPackageName()));
        filler.fill(this);
        this.fileWriter.write(endClassSting());
    }

    public void generateClass() throws IOException {
        this.generateJava((classGenerator) -> {
            this.importsFiller.fill(classGenerator);
            this.fileWriter.write(classString());
            this.classBodyFiller.fill(classGenerator);
        });
    }

    public void generateRecord() throws IOException {
        this.generateJava((classGenerator) -> {
            this.importsFiller.fill(classGenerator);
            this.fileWriter.write(recordString());
            this.classBodyFiller.fill(classGenerator);
        });
    }

    public void generateInterface() throws IOException {
        this.generateJava((classGenerator) -> {
            this.importsFiller.fill(classGenerator);
            this.fileWriter.write(interfaceString());
            this.classBodyFiller.fill(classGenerator);
        });
    }
    public void generateImport(String fullImportName) throws IOException {
        this.fileWriter.write(importString(fullImportName));
    }
    public void generateStaticImport(String fullImportName) throws IOException {
        this.fileWriter.write(staticImportString(fullImportName));
    }
    public void generateConstructor(List<DataField> args, InnerFiller constructorBody) throws IOException {
        this.fileWriter.write("\tpublic " + this.generateable.getName() +
                "(" + args.stream().map(arg -> arg.type() + " " + arg.name()).collect(Collectors.joining(", ")) + ") {\n");
        constructorBody.fill();
        this.fileWriter.write("\t}\n");
    }
    public void generateMethod(String methodName, String returnType, List<DataField> args, InnerFiller methodBody) throws IOException {
        this.fileWriter.write(methodString(methodName, returnType, args));
        methodBody.fill();
        this.fileWriter.write(endMethodString());
    }
    public void generateStaticMethod(String methodName, String returnType, DataField[] args, InnerFiller methodBody) throws IOException {
        this.fileWriter.write(staticMethodString(methodName, returnType, args));
        methodBody.fill();
        this.fileWriter.write(endMethodString());
    }
    public void generateAbstractMethod(String methodName, String returnType, List<DataField> args) throws IOException {
        this.fileWriter.write(abstractMethodString(methodName, returnType, args));
    }
    public void generateAnnotation(Annotations annotation) throws IOException {
        this.fileWriter.write("\t@" + annotation.string + "\n");
    }
    public void generateTryCatch(InnerFiller tryBlock, String exception, InnerFiller catchBlock) throws IOException {
        this.fileWriter.write("\t\ttry {\n");
        tryBlock.fill();
        this.fileWriter.write("\t\t} catch (" + exception + " e) {\n");
        catchBlock.fill();
        this.fileWriter.write("\t\t}\n");
    }
    public void generateAttribute(Access access, String attributeName, String type, String value) throws IOException {
        this.fileWriter.write("\t" + access.string + type + " " + attributeName + " = " + value + ";\n");
    }
    public void generateMethodVar(String varName, String type, String value) throws IOException {
        this.fileWriter.write("\t\t" + type + " " + varName + " = " + value + ";\n");
    }
    public static ClassBuilder builder() {
        return new ClassBuilder(new ClassGenerator());
    }
    public static class ClassBuilder implements Builder<ClassGenerator> {
        private final ClassGenerator classGenerator;
        private ClassBuilder(final ClassGenerator classGenerator) {
            this.classGenerator = classGenerator;
        }

        public ClassBuilder fileWriter(FileWriter fileWriter) {
            this.classGenerator.setFileWriter(fileWriter);
            return this;
        }
        public ClassBuilder generateable(Generateable generateable) {
            this.classGenerator.setGenerateable(generateable);
            return this;
        }
        public ClassBuilder classImports(Filler importsFiller) {
            this.classGenerator.setImportsFiller(importsFiller);
            return this;
        }
        public ClassBuilder classBody(Filler classBodyFiller) {
            this.classGenerator.setClassBodyFiller(classBodyFiller);
            return this;
        }
        public ClassBuilder classType(ClassType classType) {
            this.classGenerator.setClassType(classType);
            return this;
        }
        public ClassBuilder generic(String generic) {
            this.classGenerator.setGeneric(generic);
            return this;
        }
        public ClassBuilder implement(String implementation) {
            this.classGenerator.addImplementation(implementation);
            return this;
        }
        public ClassBuilder extend(String superClass) {
            this.classGenerator.setSuperClass(superClass);
            return this;
        }
        public ClassBuilder dataModel(DataModel dataModel) {
            this.classGenerator.setDataModel(dataModel);
            return this;
        }

        @Override
        public ClassGenerator build() {
            return this.classGenerator;
        }
    }
}
