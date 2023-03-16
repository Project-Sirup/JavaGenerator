package sirup.service.java.generator.implmentations.common;

public record Endpoint(HttpMethod method, String path, String linkedMethodName) {

    @Override
    public String toString() {
        return method().name() + " :: " + path();
    }

    public enum HttpMethod {
        GET("get"),
        POST("post"),
        PUT("put"),
        DELETE("delete");
        public final String method;
        HttpMethod(String method) {
            this.method = method;
        }
    }
}
