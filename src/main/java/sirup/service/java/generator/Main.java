package sirup.service.java.generator;

import sirup.service.java.generator.api.JavaGenApi;
//import sirup.service.java.generator.implmentations.microservice.Microservice;

public class Main {

    public static void main(String[] args) {
        //Microservice m = Microservice.fromJsonRequest(JsonTest.RPC_JSON);
        //m.make();
        new JavaGenApi().start();
    }
}
