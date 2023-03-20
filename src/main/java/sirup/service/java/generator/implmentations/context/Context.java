package sirup.service.java.generator.implmentations.context;

import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.implmentations.common.classgeneration.Access;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.Type;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.common.classgeneration.Imports;
import sirup.service.java.generator.interfaces.common.Generateable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator.*;

public class Context extends AbstractGenerateable {

    private final Microservice microservice;

    public Context(final Microservice microservice) {
        this.microservice = microservice;
    }

    public Microservice getMicroservice() {
        return this.microservice;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName + ".context";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.CLASS())
                .classImports(classGenerator -> {
                    classGenerator.generateImport(Imports.MAP);
                    classGenerator.generateImport(Imports.HASH_MAP);
                    classGenerator.generateImport(this.microservice.getDatabase().getImportString());
                    for (Generateable generateable : this.microservice.getInterfaces()) {
                        classGenerator.generateImport(generateable.getImportString());
                    }
                })
                .classBody(classGenerator -> {
                    /*
                     *
                     * Make into a builder pattern
                     *
                     */
                    classGenerator.generateAttribute(Access.PRIVATE,"services", "Map<Class<? extends Model>, Service<? extends Model>>", "new HashMap<>()");
                    classGenerator.generateAttribute(Access.PRIVATE, "database", this.microservice.getDatabase().getName(), "null");
                    classGenerator.generateMethod("addService", Type.VOID.type, new ArrayList<>(){{
                        add(new DataField("Class<? extends Model>", "modelClass"));
                        add(new DataField("Service<? extends Model>", "service"));
                    }}, () -> {
                        classGenerator.write("\t\tthis.services.put(modelClass, service);\n");
                    });
                    classGenerator.generateMethod("getService", "Service<? extends Model>", new ArrayList<>(){{
                        add(new DataField("Class<? extends Model>", "modelClass"));
                    }}, () -> {
                        classGenerator.write("\t\treturn this.services.get(modelClass);\n");
                    });
                    classGenerator.generateMethod("addDatabase", Type.VOID.type, new ArrayList<>(){{
                        add(new DataField(microservice.getDatabase().getName(), "database"));}}, () -> {
                        classGenerator.write("\t\tthis.database = database;\n");
                    });
                    classGenerator.generateMethod("getDatabase", this.microservice.getDatabase().getName(), null, () -> {
                        classGenerator.write("\t\treturn this.database;\n");
                    });
                    classGenerator.generateMethod("init", Type.VOID.type, null, () -> {
                        classGenerator.write("\t\tfor (Service<?> service : this.services.values()) {\n");
                        classGenerator.write("\t\t\tservice.addDatabase(this.database);\n");
                        classGenerator.write("\t\t};\n");
                    });
                })
                .build()
                .make();
    }

    @Override
    public String getName() {
        return "Context";
    }
}
