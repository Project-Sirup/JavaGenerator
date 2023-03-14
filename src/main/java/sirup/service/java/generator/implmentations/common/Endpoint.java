package sirup.service.java.generator.implmentations.common;

public record Endpoint(Method method, String path) {

    @Override
    public String toString() {
        return method().name() + " :: " + path();
    }

    public enum Method {
        GET("get"),
        POST("post"),
        PUT("put"),
        DELETE("delete");
        public final String method;
        Method(String method) {
            this.method = method;
        }
    }
}
