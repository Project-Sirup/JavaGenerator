package sirup.service.java.generator.api;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Scanner;

import static spark.Spark.*;

public class JavaGenApi {

    private String serviceId = Env.SERVICE_ID;
    private String serviceToken = Env.SERVICE_TOKEN;
    private String manifest;

    public JavaGenApi() {
        manifest = getManifestFromFile();
    }

    public void start() {
        if (!registerSelf()) {
            throw new RuntimeException("Could not register self!");
        }
        registerShutdown();

        port(Env.API_PORT);
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
        path(Env.API_BASE_URL, () -> {
            get("/manifest", (req, res) -> manifest);
            path("/microservice", () -> {
                MicroserviceController microserviceController = new MicroserviceController();
                post("",                microserviceController::generate);
                get("/:microserviceId", microserviceController::send);
            });
        });
    }

    private String getManifestFromFile() {
        try {
            URL url = this.getClass().getClassLoader().getResource("manifest.json");
            File docFile = new File(url.toURI());
            try (Scanner input = new Scanner(docFile)) {
                StringBuilder stringBuilder = new StringBuilder();
                while (input.hasNextLine()) {
                    stringBuilder.append(input.nextLine());
                }
                return stringBuilder.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    private void registerShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .DELETE()
                        .uri(new URI("http://127.0.0.1:2100/api/v1/" + this.serviceId))
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
                            "http://localhost:" + Env.API_PORT + Env.API_BASE_URL + "/microservice",
                            serviceId,
                            manifest),
                    serviceToken
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(registrationRequest)))
                    .uri(new URI("http://localhost:2100/api/v1"))
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
