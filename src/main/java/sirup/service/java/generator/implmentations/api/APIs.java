package sirup.service.java.generator.implmentations.api;

import sirup.service.java.generator.interfaces.api.IApi;
import sirup.service.java.generator.interfaces.api.IApiBuilder;

public final class APIs {
    public static IApiBuilder<? extends IApi> ofType(String api) {
        api = api.toLowerCase();
        switch (api) {
            case "rest", "restful" -> {
                return Rest.builder();
            }
            case "grpc", "rpc" -> {
                return Grpc.builder();
            }
            default -> throw new NoSuchApiException("API [" + api + "] is not supported");
        }
    }
}
