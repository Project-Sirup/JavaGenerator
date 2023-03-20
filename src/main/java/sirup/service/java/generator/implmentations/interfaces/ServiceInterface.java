package sirup.service.java.generator.implmentations.interfaces;

import sirup.service.java.generator.implmentations.common.classgeneration.ClassGenerator;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.Type;
import sirup.service.java.generator.implmentations.common.classgeneration.ClassTypes;
import sirup.service.java.generator.implmentations.common.classgeneration.Imports;
import sirup.service.java.generator.implmentations.context.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ServiceInterface extends AbstractInterface {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        ClassGenerator.builder()
                .fileWriter(fileWriter)
                .generateable(this)
                .classType(ClassTypes.INTERFACE())
                .generic("T extends Model")
                .classImports(classGenerator -> {
                    classGenerator.generateImport(this.context.getMicroservice().getDatabase().getImportString());
                    classGenerator.generateImport(Imports.LIST);
                })
                .classBody(classGenerator -> {
                    classGenerator.generateAbstractMethod("addDatabase", Type.VOID.type, new ArrayList<>(){{
                        add(new DataField(context.getMicroservice().getDatabase().getName(), "database"));}});
                    classGenerator.generateAbstractMethod("getAll", "List<T>", null);
                    classGenerator.generateAbstractMethod("get", "T", new ArrayList<>(){{add(new DataField(Type.STRING.type, "id"));}});
                    classGenerator.generateAbstractMethod("add", Type.BOOLEAN.type, new ArrayList<>(){{add(new DataField("T", "t"));}});
                    classGenerator.generateAbstractMethod("update", Type.BOOLEAN.type, new ArrayList<>(){{add(new DataField("T", "t"));}});
                    classGenerator.generateAbstractMethod("remove", Type.BOOLEAN.type, new ArrayList<>(){{add(new DataField(Type.STRING.type, "id"));}});
                })
                .build()
                .make();
    }

    @Override
    public String getName() {
        return "Service";
    }
}
