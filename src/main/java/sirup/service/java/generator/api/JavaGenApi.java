package sirup.service.java.generator.api;

import static spark.Spark.*;

public class JavaGenApi {

    private static final String API_BASE_URL = "/api/v1";

    public void start() {
        port(Env.PORT);
        path(API_BASE_URL, () -> {
            get("/manifest", (req, res) -> "manifest.json");
            path("/microservice", () -> {
                MicroserviceController microserviceController = new MicroserviceController();
                post("",                microserviceController::generate);
                get("/:microserviceId", microserviceController::send);
            });
        });
    }
}
