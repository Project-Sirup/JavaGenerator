package sirup.service.java.generator;

import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.implmentations.api.APIs;
import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.BuildTools;
import sirup.service.java.generator.implmentations.buildtool.Maven;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.EndpointGroup;
import sirup.service.java.generator.implmentations.database.Databases;
import sirup.service.java.generator.implmentations.database.PostgreSQL;
import sirup.service.java.generator.implmentations.common.DataModel;
import sirup.service.java.generator.implmentations.common.Endpoint;

import java.util.ArrayList;
import java.util.List;

import static sirup.service.java.generator.implmentations.common.Endpoint.Method.*;
import static sirup.service.java.generator.implmentations.common.DataField.Type.*;

public class Main {
    public static void main(String[] args) {
        // ----- Test data ----- Will come from json -----
        String serviceName = "marco";
        String packageName = "dk.smu.mmmi";
        EndpointGroup userGroup = EndpointGroup.builder()
                .groupName("/user")
                .endpoint(GET, "/:userId")
                .endpoint(GET, "")
                .endpoint(POST, "")
                .endpoint(PUT, "")
                .endpoint(DELETE, "/:userId")
                .build();
        EndpointGroup v1Grouo = EndpointGroup.builder()
                .groupName("/v1")
                .innerGrouo(userGroup)
                .build();
        EndpointGroup apiGroup = EndpointGroup.builder()
                .groupName("/api")
                .innerGrouo(v1Grouo)
                .build();

        String dataModelName = "user";
        List<DataField> dataFields = new ArrayList<>(){{
           add(new DataField(STRING, "name"));
           add(new DataField(STRING, "password"));
           add(new DataField(STRING, "userId"));
           add(new DataField(INT32, "age"));
           add(new DataField(BOOLEAN, "verified"));
        }};

        // ----- Rest Builder -----
        Rest.RestBuilder restBuilder = APIs.restBuilder();
        restBuilder.endpontGroup(apiGroup);

        // ----- Database Builder -----
        PostgreSQL.PostgreSQLBuilder postgreSQLBuilder = Databases.postgreSQLBuilder();

        DataModel.DataModelBuilder dataModelBuilder = DataModel.builder().name(dataModelName);
        dataFields.forEach(dataModelBuilder::dataField);

        postgreSQLBuilder.dataModel(dataModelBuilder);

        // ----- BuildTool Builder -----
        Maven.MavenBuilder mavenBuilder = BuildTools.mavenBuilder();

        // ----- Microservice Builder -----
        Microservice microservice = Microservice
                .builder()
                .name(serviceName)
                .packageName(packageName)
                .api(restBuilder)
                .database(postgreSQLBuilder)
                .buildTool(mavenBuilder)
                .build();

        //System.out.println(microservice);
        microservice.make();
    }
}
