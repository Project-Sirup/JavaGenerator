package sirup.service.java.generator.implmentations.api;

public final class APIs {
    public static Rest.RestBuilder restBuilder() {
        return Rest.builder();
    }
    public static Grpc.GrpcBuilder grpcBuilder() {
        return Grpc.builder();
    }
}
