package sirup.service.java.generator;

import sirup.service.java.generator.implmentations.Microservice;

public class Main {

    public static void main(String[] args) {
        Microservice m = Microservice.fromJsonRequest(JsonTest.json);

        m.make();
    }
}
