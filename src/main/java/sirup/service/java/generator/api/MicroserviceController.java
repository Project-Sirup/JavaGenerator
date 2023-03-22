package sirup.service.java.generator.api;

import com.google.gson.Gson;
import sirup.service.java.generator.implmentations.microservice.LanguageNotSupportedException;
import sirup.service.java.generator.implmentations.microservice.Microservice;
import sirup.service.java.generator.implmentations.api.NoSuchApiException;
import sirup.service.java.generator.implmentations.buildtool.NoSuchBuildToolException;
import sirup.service.java.generator.implmentations.database.NoSuchDatabaseException;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import static sirup.service.java.generator.implmentations.common.StringUtil.*;
import static spark.Spark.halt;

public class MicroserviceController {

    private final Gson gson = new Gson();

    public Object generate(Request request, Response response) {
        try {
            MicroserviceRequest microserviceRequest = gson.fromJson(request.body(), MicroserviceRequest.class);
            Microservice microservice = Microservice.fromJsonRequest(microserviceRequest);
            microservice.make();
            return "http://127.0.0.1:4567/api/v1/microservice/" + microserviceRequest.microservice().microserviceId();
        } catch (NoSuchApiException | NoSuchDatabaseException | NoSuchBuildToolException | LanguageNotSupportedException e) {
            e.printStackTrace();
            halt(405, e.getMessage());
        }

        return "Something went wrong :/";
    }

    public Object send(Request request, Response response) {
        String microserviceId = request.params("microserviceId");
        File dir = new File(BASE_DIR + "/" + microserviceId);
        if (!dir.exists()) {
            halt(404, "Microservice with id = " + microserviceId + " was not found");
        }
        response.raw().setContentType("application/octet-stream");
        response.raw().setHeader("Content-Disposition","attachment; filename=" + dir.getName() + ".zip");
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.raw().getOutputStream());
            FileZipper.zip(dir, dir.getName(), zipOutputStream);
            zipOutputStream.close();
        } catch (IOException e) {
            halt(405, "Server error!");
        }
        return response.raw();
    }
}
