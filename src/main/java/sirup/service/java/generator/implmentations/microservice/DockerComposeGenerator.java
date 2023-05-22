package sirup.service.java.generator.implmentations.microservice;

import sirup.service.java.generator.implmentations.common.AbstractGenerateable;
import sirup.service.java.generator.interfaces.common.DockerService;

import java.io.FileWriter;
import java.io.IOException;

import static sirup.service.java.generator.implmentations.common.StringUtil.tab;

public class DockerComposeGenerator extends AbstractGenerateable {

    private final DockerService[] dockerServices;

    public DockerComposeGenerator(DockerService ...dockerServices) {
        this.dockerServices = dockerServices;
    }

    @Override
    public String getName() {
        return "docker-compose.yml";
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public String getDir() {
        return "";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("services:\n");
        for (DockerService ds : this.dockerServices) {
            fileWriter.write(tab(1) + ds.getName() + ":\n");
            fileWriter.write(tab(2) + "build:\n");
            fileWriter.write(tab(3) + ds.getBuildContext() + "\n");
            fileWriter.write(tab(2) + "ports:\n");
            fileWriter.write(tab(3) + "- " + ds.getExternalPort() + ":" + ds.getInternalPort() + "\n");
        }
    }
}
