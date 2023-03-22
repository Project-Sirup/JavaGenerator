package sirup.service.java.generator.api;

import spark.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileZipper {

    public static void zip(File file, String fileName, ZipOutputStream zipOutputStream) throws IOException {
        if (file.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
            }
            else {
                zipOutputStream.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOutputStream.closeEntry();
            for (File subFile : file.listFiles()) {
                zip(subFile, fileName + "/" + subFile.getName(), zipOutputStream);
            }
            return;
        }

        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        FileInputStream fileInputStream = new FileInputStream(file);
        IOUtils.copy(fileInputStream, zipOutputStream);
        fileInputStream.close();
        //zipOutputStream.closeEntry();
    }
}
