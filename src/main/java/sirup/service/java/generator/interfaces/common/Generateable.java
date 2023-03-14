package sirup.service.java.generator.interfaces.common;

import java.io.FileWriter;
import java.io.IOException;

public interface Generateable extends Nameable {
    void setPackageName(String packageName);
    String getPackageName();
    String getDir();
    String getImportString();
    void fillFile(FileWriter fileWriter) throws IOException;
}
