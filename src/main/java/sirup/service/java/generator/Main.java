package sirup.service.java.generator;

import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.Maven;
import sirup.service.java.generator.implmentations.database.PostgreSQL;
import sirup.service.java.generator.implmentations.misc.DataModel;
import sirup.service.java.generator.implmentations.misc.Endpoint;

import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.misc.Endpoint.Method.*;
import static sirup.service.java.generator.implmentations.misc.DataField.Type.*;

public class Main {
    public static void main(String[] args) {
        // ----- Test data -----
        List<Endpoint> endpoints = new ArrayList<>(){{
            add(new Endpoint(GET, "/api/v1/user/:userId"));
            add(new Endpoint(POST, "/api/v1/user"));
            add(new Endpoint(PUT, "/api/v1/user"));
            add(new Endpoint(DELETE, "/api/v1/user/.userId"));
        }};

        // ----- Rest Builder -----
        Rest.RestBuilder restBuilder = Rest.builder();
        endpoints.forEach(restBuilder::endpoint);

        // ----- Database Builder -----
        PostgreSQL.PostgreSQLBuilder postgreSQLBuilder = PostgreSQL.builder();
        postgreSQLBuilder.dataModel(DataModel
                .builder()
                .name("user")
                .dataField(STRING, "name")
                .dataField(STRING, "password")
                .dataField(STRING, "userId")
                .dataField(INT32, "age")
                .dataField(BOOLEAN, "verified"));

        // ----- BuildTool Builder -----
        Maven.MavenBuilder mavenBuilder = Maven.builder();

        // ----- Microservice Builder -----
        Microservice microservice = Microservice
                .builder()
                .name("macro")
                .packageName("dk.sdu.mmmi")
                .api(restBuilder.build())
                .database(postgreSQLBuilder.build())
                .buildTool(mavenBuilder.build())
                .build();

        System.out.println(microservice);
        microservice.make();
    }
}
