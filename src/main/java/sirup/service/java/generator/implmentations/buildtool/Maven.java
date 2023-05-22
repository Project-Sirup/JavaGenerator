package sirup.service.java.generator.implmentations.buildtool;

import sirup.service.java.generator.interfaces.common.Dependency;
import sirup.service.java.generator.interfaces.buildtool.IBuildTool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static sirup.service.java.generator.implmentations.common.StringUtil.tab;

public final class Maven extends AbstractBuildTool implements IBuildTool {

    public static final Maven DEFAULT;

    private final static Map<String, String> DEPENDENCY_MAP;

    private final List<String> dependencies;

    static {
        DEFAULT = new Maven();
        DEPENDENCY_MAP = new HashMap<>(){{
            put("rest", """
                            <!-- REST -->
                            <dependency>
                                <groupId>com.sparkjava</groupId>
                                <artifactId>spark-core</artifactId>
                                <version>2.9.4</version>
                            </dependency>
                            <dependency>
                                <groupId>com.google.code.gson</groupId>
                                <artifactId>gson</artifactId>
                                <version>2.10</version>
                            </dependency>
                            <dependency>
                                <groupId>org.slf4j</groupId>
                                <artifactId>slf4j-api</artifactId>
                                <version>2.0.5</version>
                            </dependency>
                            <dependency>
                                <groupId>org.slf4j</groupId>
                                <artifactId>slf4j-simple</artifactId>
                                <version>2.0.5</version>
                            </dependency>
                            <!-- REST -->
                    """);
            put("rcp", """
                            <!-- RPC -->
                            <dependency>
                                <groupId>io.grpc</groupId>
                                <artifactId>grpc-netty-shaded</artifactId>
                                <version>1.15.1</version>
                            </dependency>
                            <dependency>
                                <groupId>io.grpc</groupId>
                                <artifactId>grpc-protobuf</artifactId>
                                <version>1.15.1</version>
                            </dependency>
                            <dependency>
                                <groupId>io.grpc</groupId>
                                <artifactId>grpc-stub</artifactId>
                                <version>1.15.1</version>
                            </dependency>
                            <!-- gRPC -->
                    """);
        }};
    }

    Maven () {
        this.dependencies = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "pom.xml";
    }

    @Override
    public void updateDependencies(Dependency ...dependencies) {
        for (Dependency dependency : dependencies) {
            this.dependencies.add(DEPENDENCY_MAP.getOrDefault(dependency.getDependencyName(),""));
        }
    }

    @Override
    public String toString() {
        return "maven: {\n" +
                "dependencies: {\n" +
                this.dependencies.stream().map(Object::toString).collect(Collectors.joining("\n")) + "\n" +
                "}";
    }

    @Override
    public void fillFile(FileWriter fileWriter) throws IOException {
        fileWriter.write("""
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                          
                    <groupId>dk.sdu.mmmi</groupId>
                    <artifactId>Macro</artifactId>
                    <version>1.0-SNAPSHOT</version>
                          
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-compiler-plugin</artifactId>
                                <version>3.8.0</version>
                                <configuration>
                                    <source>17</source>
                                    <target>17</target>
                                </configuration>
                            </plugin>
                            <plugin>
                                <groupId>org.codehaus.mojo</groupId>
                                <artifactId>exec-maven-plugin</artifactId>
                                    <version>3.0.0</version>
                                    <configuration>
                                        <mainClass>dk.sdu.mmmi.Macro</mainClass>
                                    </configuration>
                            </plugin>
                            <plugin>
                              <groupId>org.apache.maven.plugins</groupId>
                              <artifactId>maven-shade-plugin</artifactId>
                              <executions>
                                  <execution>
                                      <goals>
                                          <goal>shade</goal>
                                      </goals>
                                      <configuration>
                                          <shadedArtifactAttached>true</shadedArtifactAttached>
                                          <transformers>
                                              <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                                  <mainClass>
                                                  """ + tab(9) + this.packageName + ".Main" +
                                                """
                                              
                                                  </mainClass>
                                              </transformer>
                                          </transformers>
                                      </configuration>
                                  </execution>
                              </executions>
                          </plugin>
                        </plugins>
                    </build>
                    <properties>
                        <maven.compiler.source>17</maven.compiler.source>
                        <maven.compiler.target>17</maven.compiler.target>
                    </properties>
                    <dependencies>
                   """ +
                String.join("", this.dependencies)
                +
             """
                <dependency>
                    <groupId>io.github.cdimascio</groupId>
                    <artifactId>java-dotenv</artifactId>
                    <version>5.2.2</version>
                </dependency>
                <dependency>
                    <groupId>junit</groupId>
                    <artifactId>junit</artifactId>
                    <version>4.12</version>
                    <scope>test</scope>
                </dependency>
            </dependencies>
          </project>
          """
        );
    }
}
