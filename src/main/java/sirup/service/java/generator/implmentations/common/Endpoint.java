package sirup.service.java.generator.implmentations.common;

import java.util.Locale;

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
        public static HttpMethod from(String string) {
            string = string.toLowerCase();
            switch (string) {
                default -> {
                    return GET;
                }
                case "post" -> {
                    return POST;
                }
                case "put" -> {
                    return PUT;
                }
                case "delete" -> {
                    return DELETE;
                }
            }
        }
    }
}
