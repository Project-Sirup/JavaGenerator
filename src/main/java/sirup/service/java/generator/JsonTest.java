package sirup.service.java.generator;

public class JsonTest {

    public static final String RPC_JSON = """
            {
                "sirup_v": 1,
                "docker": true,
                "microservice": {
                    "microserviceId": "ms02",
                    "microserviceName": "macro",
                    "language": {
                        "name": "java",
                        "options": {
                            "buildTool": "maven",
                            "groupId": "du.sdu.mmmi"
                        }
                    },
                    "database": {
                        "name": "postgresql",
                        "options": {
                        },
                        "data": {
                            "collections": [
                                {
                                    "name": "user",
                                    "fields": [
                                        {
                                            "name": "userId",
                                            "type": "string"
                                        },
                                        {
                                            "name": "userName",
                                            "type": "string"
                                        },
                                        {
                                            "name": "password",
                                            "type": "string"
                                        },
                                        {
                                            "name": "age",
                                            "type": "int32"
                                        },
                                        {
                                            "name": "verified",
                                            "type": "boolean"
                                        }
                                    ]
                                },
                                {
                                    "name": "task",
                                    "fields": [
                                        {
                                            "name": "taskId",
                                            "type": "string"
                                        },
                                        {
                                            "name": "taskName",
                                            "type": "string"
                                        },
                                        {
                                            "name": "userId",
                                            "type": "string",
                                            "ref": "user.userId"
                                        }
                                    ]
                                }
                            ]
                        }
                    },
                    "api": {
                        "type": "grpc",
                        "options": {
                            "port": 4567,
                            "procedures": [
                                {
                                    "procedureName": "makeToken",
                                    "requestMessage": "tokenRequest",
                                    "responseMessage": "tokenResponse"
                                },
                                {
                                    "procedureName": "authToken",
                                    "requestMessage": "authRequest",
                                    "responseMessage": "authResponse"
                                }
                            ],
                            "messages": [
                                {
                                    "messageName": "credentials",
                                    "fields": [
                                        {
                                            "fieldName": "userId",
                                            "fieldType": "string"
                                        }
                                    ]
                                },
                                {
                                    "messageName": "tokenRequest",
                                    "fields": [
                                        {
                                            "fieldName": "credentials",
                                            "fieldType": "credentials"
                                        }
                                    ]
                                },
                                {
                                    "messageName": "tokenResponse",
                                    "fields": [
                                        {
                                            "fieldName": "token",
                                            "fieldType": "string"
                                        }
                                    ]
                                },
                                {
                                    "messageName": "authRequest",
                                    "fields": [
                                        {
                                            "fieldName": "token",
                                            "fieldType": "string"
                                        },
                                        {
                                            "fieldName": "credentials",
                                            "fieldType": "credentials"
                                        }
                                    ]
                                },
                                {
                                    "messageName": "authResponse",
                                    "fields": [
                                        {
                                            "fieldName": "valid",
                                            "fieldType": "boolean"
                                        }
                                    ]
                                }
                            ]
                        }
                    },
                    "external": {
                        "name": "externalService"
                    }
                }
            }
            """;

    public static final String REST_JSON = """
            {
                "sirup_v": 1,
                "docker": true,
                "microservice": {
                    "microserviceId": "ms01",
                    "microserviceName": "macro",
                    "language": {
                        "name": "java",
                        "options": {
                            "buildTool": "maven",
                            "groupId": "dk.sdu.mmmi"
                        }
                    },
                    "database": {
                        "name": "postgresql",
                        "options": {
                        },
                        "data": {
                            "collections": [
                                {
                                    "name": "user",
                                    "fields": [
                                        {
                                            "name": "userId",
                                            "type": "string"
                                        },
                                        {
                                            "name": "userName",
                                            "type": "string"
                                        },
                                        {
                                            "name": "password",
                                            "type": "string"
                                        },
                                        {
                                            "name": "age",
                                            "type": "int32"
                                        },
                                        {
                                            "name": "verified",
                                            "type": "boolean"
                                        }
                                    ]
                                },
                                {
                                    "name": "task",
                                    "fields": [
                                        {
                                            "name": "taskId",
                                            "type": "string"
                                        },
                                        {
                                            "name": "taskName",
                                            "type": "string"
                                        },
                                        {
                                            "name": "userId",
                                            "type": "string",
                                            "ref": "user.userId"
                                        }
                                    ]
                                }
                            ]
                        }
                    },
                    "api": {
                        "type": "rest",
                        "options": {
                            "port": "4567",
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
                                                            "path": "/:userId",
                                                            "linkedMethod": "find"
                                                        },
                                                        {
                                                            "method": "get",
                                                            "path": "",
                                                            "linkedMethod": "findAll"
                                                        },
                                                        {
                                                            "method": "post",
                                                            "path": "",
                                                            "linkedMethod": "create"
                                                        },
                                                        {
                                                            "method": "put",
                                                            "path": "",
                                                            "linkedMethod": "update"
                                                        },
                                                        {
                                                            "method": "delete",
                                                            "path": "/:userId",
                                                            "linkedMethod": "remove"
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
                                                            "path": "/:taskId",
                                                            "linkedMethod": "find"
                                                        },
                                                        {
                                                            "method": "get",
                                                            "path": "",
                                                            "linkedMethod": "findAll"
                                                        },
                                                        {
                                                            "method": "post",
                                                            "path": "",
                                                            "linkedMethod": "create"
                                                        },
                                                        {
                                                            "method": "put",
                                                            "path": "",
                                                            "linkedMethod": "update"
                                                        },
                                                        {
                                                            "method": "delete",
                                                            "path": "/:taskId",
                                                            "linkedMethod": "remove"
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
                        }
                    },
                    "external": {
                        "name": "externalService"
                    }
                }
            }
            """;
}
