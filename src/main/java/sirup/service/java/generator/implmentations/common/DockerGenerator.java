package sirup.service.java.generator.implmentations.common;

import sirup.service.java.generator.interfaces.common.Builder;

import java.util.ArrayList;
import java.util.List;

public class DockerGenerator {


    private DockerGenerator() {
    }

    public static DockerBuilder builder() {
        return new DockerBuilder();
    }

    public void make() {

    }

    public static class DockerBuilder implements Builder<DockerGenerator> {

        private final DockerGenerator dockerGenerator;
        private final StringBuilder stringBuilder;

        private DockerBuilder() {
            this.dockerGenerator = new DockerGenerator();
            this.stringBuilder = new StringBuilder();
        }

        public DockerBuilder from(String image) {
            this.stringBuilder.append("FROM ").append(image).append("\n");
            return this;
        }

        public DockerBuilder workdir(String workdir) {
            this.stringBuilder.append("WORKDIR ").append(workdir).append("\n");
            return this;
        }

        public DockerBuilder env(String var, String value) {
            this.stringBuilder.append("ENV ").append(var).append(" ").append(value).append("\n");
            return this;
        }

        public DockerBuilder copy(String externalPath, String internalPath) {
            this.stringBuilder.append("COPY ").append(externalPath).append(" ").append(internalPath).append("\n");
            return this;
        }

        public DockerBuilder run(String... commands) {
            wrapCommands(commands);
            this.stringBuilder.append("RUN [").append(String.join(",", commands)).append("]\n");
            return this;
        }

        public DockerBuilder run(String command) {
            this.stringBuilder.append("RUN ").append(command).append("\n");
            return this;
        }

        public DockerBuilder cmd(String... commands) {
            wrapCommands(commands);
            this.stringBuilder.append("CMD [").append(String.join(",", commands)).append("]\n");
            return this;
        }

        public DockerBuilder cmd(String command) {
            this.stringBuilder.append("CMD ").append(command).append("\n");
            return this;
        }

        public DockerBuilder entrypoint(String... commands) {
            wrapCommands(commands);
            this.stringBuilder.append("ENTRYPOINT [").append(String.join(",", commands)).append("]\n");
            return this;
        }

        public DockerBuilder entrypoint(String command) {
            this.stringBuilder.append("ENTRYPOINT ").append(command).append("\n");
            return this;
        }

        private void wrapCommands(String[] commands) {
            for (int i = 0; i < commands.length; i++) {
                commands[i] = "\"" + commands[i] + "\"";
            }
        }

        @Override
        public DockerGenerator build() {
            return dockerGenerator;
        }
    }
}
