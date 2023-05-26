package sirup.service.java.generator;

import sirup.service.java.generator.api.Env;
import sirup.service.java.generator.api.JavaGenApi;
import sirup.service.log.rpc.client.LogClient;
//import sirup.service.java.generator.implmentations.microservice.Microservice;

public class Main {

    public static void main(String[] args) {
        //Microservice m = Microservice.fromJsonRequest(JsonTest.RPC_JSON);
        //m.make();
        LogClient.init(Env.LOG_ADDRESS, Env.LOG_PORT, "JavaGenService");
        new JavaGenApi().start();
    }
}
