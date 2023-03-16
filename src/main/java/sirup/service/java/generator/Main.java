package sirup.service.java.generator;

import com.google.gson.Gson;
import sirup.service.java.generator.api.MicroserviceRequest;
import sirup.service.java.generator.implmentations.Microservice;
import sirup.service.java.generator.implmentations.api.APIs;
import sirup.service.java.generator.implmentations.api.Rest;
import sirup.service.java.generator.implmentations.buildtool.BuildTools;
import sirup.service.java.generator.implmentations.buildtool.Maven;
import sirup.service.java.generator.implmentations.common.DataField;
import sirup.service.java.generator.implmentations.common.EndpointGroup;
import sirup.service.java.generator.implmentations.controller.Controller;
import sirup.service.java.generator.implmentations.database.Databases;
import sirup.service.java.generator.implmentations.database.PostgreSQL;
import sirup.service.java.generator.implmentations.model.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static sirup.service.java.generator.implmentations.common.Endpoint.HttpMethod.*;
import static sirup.service.java.generator.implmentations.common.DataField.Type.*;

public class Main {

    public static final String json = """
            {
                  "sirup_v": 1,
                  "docker": true,
                  "microservice": {
                      "microserviceId": "<uuid>",
                      "microserviceName": "my_user_service",
                      "language": {
                          "name": "java",
                          "options": {
                              "buildTool": "maven",
                              "packageName": "du.sdu.mmmi"
                          }
                      },
                      "database": {
                          "name": "postgresql",
                          "options": {
                              "version": "<version>"
                          },
                          "data": {
                              "collections": [
                                  {
                                      "name": "user",
                                      "fields": [
                                          {
                                              "name": "userName",
                                              "type": "string"
                                          },
                                          {
                                              "name": "userId",
                                              "type": "string"
                                          }
                                      ]
                                  },
                                  {
                                      "name": "task",
                                      "fields": [
                                          {
                                              "name": "taskName",
                                              "type": "string"
                                          },
                                          {
                                              "name": "taskId",
                                              "type": "string"
                                          },
                                          {
                                              "name": "userId",
                                              "type": "string"
                                          }
                                      ]
                                  }
                              ]
                          }
                      },
                      "api": {
                          "type": "rest",
                          "useAsDocs": true,
                          "endpointGroups": [
                              {
                                  "groupName": "/api",
                                  "innerGroups": [
                                      {
                                          "groupName": "/v1",
                                          "innerGroups": [
                                              {
                                                  "groupName": "/user",
                                                  "innerGroups": [],
                                                  "endpoints": [
                                                      {
                                                          "method": "get",
                                                          "path": ""
                                                      },
                                                      {
                                                          "method": "get",
                                                          "path": "/:userId"
                                                      },
                                                      {
                                                          "method": "post",
                                                          "path": ""
                                                      },
                                                      {
                                                          "method": "put",
                                                          "path": ""
                                                      },
                                                      {
                                                          "method": "delete",
                                                          "path": "/:userId"
                                                      }
                                                  ],
                                                  "linkedData": "user"
                                              },
                                              {
                                                  "groupName": "/task",
                                                  "innerGroups": [],
                                                  "endpoints": [
                                                      {
                                                          "method": "get",
                                                          "path": ""
                                                      },
                                                      {
                                                          "method": "get",
                                                          "path": "/:taskId"
                                                      },
                                                      {
                                                          "method": "post",
                                                          "path": ""
                                                      },
                                                      {
                                                          "method": "put",
                                                          "path": ""
                                                      },
                                                      {
                                                          "method": "delete",
                                                          "path": "/:taskId"
                                                      }
                                                  ],
                                                  "linkedData": "task"
                                              }
                                          ],
                                          "endpoints": [],
                                          "linkedData": null
                                      }
                                  ],
                                  "endpoints": [],
                                  "linkedData": null
                              }
                          ],
                          "endpoints": []
                      },
                      "external": {
                          "name": "externalService"
                      }
                  }
              }
            """;

    public static void main(String[] args) {
        testLogic(testRecords());
    }

    public static MicroserviceRequest testRecords() {
        Gson gson = new Gson();
        MicroserviceRequest m = gson.fromJson(json, MicroserviceRequest.class);
        System.out.println(m);
        return m;
    }

    public static void testLogic(MicroserviceRequest m) {
        // ----- Test data ----- Will come from json -----
        List<DataField> userFields = new ArrayList<>(){{
            add(new DataField(STRING, "name"));
            add(new DataField(STRING, "password"));
            add(new DataField(STRING, "userId"));
            add(new DataField(INT32, "age"));
            add(new DataField(BOOLEAN, "verified"));
        }};
        DataModel.DataModelBuilder userModelBuilder = DataModel.builder().name("user");
        userFields.forEach(userModelBuilder::dataField);
        DataModel userModel = userModelBuilder.build();

        List<DataField> taskFields = new ArrayList<>(){{
            add(new DataField(STRING, "name"));
            add(new DataField(STRING, "description"));
            add(new DataField(STRING, "taskId"));
            add(new DataField(STRING, "userId"));
            add(new DataField(BOOLEAN, "completed"));
        }};
        DataModel.DataModelBuilder taskModelBuilder = DataModel.builder().name("task");
        taskFields.forEach(taskModelBuilder::dataField);
        DataModel taskModel = taskModelBuilder.build();

        String serviceName = "macro";
        String packageName = "dk.sdu.mmmi";
        EndpointGroup userGroup = EndpointGroup.builder()
                .groupName("/user")
                .controller(Controller.of(userModel))
                .endpoint(GET, "/:userId", "find")
                .endpoint(GET, "", "findAll")
                .endpoint(POST, "", "create")
                .endpoint(PUT, "", "update")
                .endpoint(DELETE, "/:userId", "remove")
                .build();
        EndpointGroup taskGroup = EndpointGroup.builder()
                .groupName("/task")
                .controller(Controller.of(taskModel))
                .endpoint(GET, "/:taskId", "find")
                .endpoint(GET, "", "findAll")
                .endpoint(GET, "/:userId", "findUser")
                .endpoint(POST, "", "create")
                .endpoint(PUT, "", "update")
                .endpoint(DELETE, "/:taskId", "remove")
                .build();
        EndpointGroup v1Group = EndpointGroup.builder()
                .groupName("/v1")
                .innerGroup(userGroup)
                .innerGroup(taskGroup)
                .endpoint(GET, "", null)
                .build();
        EndpointGroup v2Group = EndpointGroup.builder()
                .groupName("/v2")
                .endpoint(GET, "", null)
                .build();
        EndpointGroup apiGroup = EndpointGroup.builder()
                .groupName("/api")
                .innerGroup(v1Group)
                .innerGroup(v2Group)
                .build();

        // ----- Rest Builder -----
        Rest restApi = APIs.restBuilder()
                .endpointGroup(apiGroup)
                .build();

        // ----- Database Builder -----
        PostgreSQL postgreSQL = Databases.postgreSQLBuilder()
                .dataModel(userModel)
                .dataModel(taskModel)
                .build();

        // ----- BuildTool Builder -----
        Maven maven = BuildTools.mavenBuilder().build();

        // ----- Microservice Builder -----
        Microservice microservice = Microservice
                .builder()
                .name(serviceName)
                .packageName(packageName)
                .api(restApi)
                .database(postgreSQL)
                .buildTool(maven)
                .build();

        //System.out.println(microservice);
        microservice.make();
    }
}
