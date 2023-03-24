package sirup.service.java.generator.api;

import static spark.Spark.*;

public class JavaGenApi {

    public void start() {
        port(Env.API_PORT);
        path(Env.API_BASE_URL, () -> {
            get("/manifest", (req, res) -> "manifest.json");
            path("/microservice", () -> {
                MicroserviceController microserviceController = new MicroserviceController();
                post("",                microserviceController::generate);
                get("/:microserviceId", microserviceController::send);
            });
        });
    }
}
