package sirup.service.java.generator;

import sirup.service.java.generator.api.RequestParser;
import sirup.service.java.generator.implmentations.Microservice;

public class Main {

    public static void main(String[] args) {
        Microservice m = RequestParser.fromJsonRequest(JsonTest.json);

        m.make();
    }
}
