package sirup.service.java.generator;

import sirup.service.java.generator.api.JavaGenApi;

public class Main {

    public static void main(String[] args) {
        //Microservice m = Microservice.fromJsonRequest(JsonTest.RPC_JSON);
        //m.make();
        new JavaGenApi().start();
    }
}
