package sirup.service.java.generator.implmentations.misc;

public record Endpoint(Method method, String path) {

    @Override
    public String toString() {
        return method().name() + " :: " + path();
    }

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }
}
