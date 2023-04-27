package sirup.service.java.generator.api;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    public static final int API_PORT;
    public static final String API_BASE_URL;
    public static final String SERVICE_TOKEN;
    public static final String SERVICE_ID;
    static {
        Dotenv dotenv = Dotenv.load();
        API_PORT = Integer.parseInt(dotenv.get("API_PORT"));
        API_BASE_URL = dotenv.get("API_BASE_URL");
        SERVICE_TOKEN = dotenv.get("SERVICE_TOKEN");
        SERVICE_ID = dotenv.get("SERVICE_ID");
    }
}
