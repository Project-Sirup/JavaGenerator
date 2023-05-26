package sirup.service.java.generator.api;

import com.google.gson.Gson;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static spark.Spark.*;

public class JavaGenApi {

    private String serviceId = Env.JAVA_GEN_SERVICE_ID;
    private String serviceToken = Env.JAVA_GEN_SERVICE_TOKEN;
    private String manifest;

    public JavaGenApi() {
        manifest = getManifestFromFile();
    }

    public void start() {
        if (!registerSelf()) {
            throw new RuntimeException("Could not register self!");
        }
        registerShutdown();

        port(Env.JAVA_GEN_PORT);
        before("*",(request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "*");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Credentials-Header", "*");
            response.header("Accept", "*/*");
            //response.header("Content-Type", "text/event-stream");
        });
        options("*", ((request, response) -> {
            Optional.ofNullable(request.headers("Access-Control-Request-Headers"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Headers", header));
            Optional.ofNullable(request.headers("Access-Control-Request-Method"))
                    .ifPresent(header -> response.header("Access-Control-Allow-Methods", header));
            Optional.ofNullable(request.headers("Accept"))
                    .ifPresent(header -> response.header("Accept", header));
            response.status(200);
            return "OK";
        }));
        path("/api/v1", () -> {
            get("/health", (req, res) -> "ok");
            get("/manifest", (req, res) -> manifest);
            path("/microservice", () -> {
                MicroserviceController microserviceController = new MicroserviceController();
                post("",                microserviceController::generate);
                get("/:microserviceId", microserviceController::send);
            });
        });
    }

    private String getManifestFromFile() {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("manifest.json")) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            inputStreamReader.close();
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .DELETE()
                        .uri(new URI("http://registerservice:2100/api/v1/register/" + this.serviceId))
                        .build();
                HttpClient client = HttpClient.newBuilder().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    private boolean registerSelf() {
        try {
            Gson gson = new Gson();
            RegistrationRequest registrationRequest = new RegistrationRequest(
                    new RegistrationRequest.Registration(
                            "JavaGenerationService",
                            Env.JAVA_GEN_ADDRESS + ":" + Env.JAVA_GEN_PORT + "/api/v1/microservice",
                            serviceId,
                            manifest),
                    serviceToken
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(registrationRequest)))
                    .uri(new URI("http://registerservice:2100/api/v1/register"))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            return response.statusCode() == 200;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
    private record RegistrationRequest(Registration registration, String serviceToken) {
        private record Registration(String serviceName, String serviceAddress, String serviceId, Object manifest) {}
    }
}
