package sirup.service.java.generator.api;

public class Env {
    public static final String JAVA_GEN_ADDRESS;
    public static final int JAVA_GEN_PORT;
    public static final String JAVA_GEN_SERVICE_TOKEN;
    public static final String JAVA_GEN_SERVICE_ID;
    public static final String LOG_ADDRESS;
    public static final int LOG_PORT;
    public static final String REG_ADDRESS;
    public static final int REG_PORT;
    static {
        JAVA_GEN_ADDRESS = System.getenv("JAVA_GEN_ADDRESS");
        JAVA_GEN_PORT = Integer.parseInt(System.getenv("JAVA_GEN_PORT"));
        JAVA_GEN_SERVICE_TOKEN = System.getenv("JAVA_GEN_SERVICE_TOKEN");
        JAVA_GEN_SERVICE_ID = System.getenv("JAVA_GEN_SERVICE_ID");
        LOG_ADDRESS = System.getenv("LOG_ADDRESS");
        LOG_PORT = Integer.parseInt(System.getenv("LOG_PORT"));
        REG_ADDRESS = System.getenv("REG_ADDRESS");
        REG_PORT = Integer.parseInt(System.getenv("REG_PORT"));
    }
}
