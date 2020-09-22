# Introduction
This document defines gRPC api to interact with the Hivilake system.

# Service point of view:
There are 3 services:

1. setup - for normal 1 request-repsonse connection.
2. upload_file - for streaming connection to upload long data.
3. download_file - for streaming connection return long data.

With 3 type of message:

1. ActionRequest - request message of normal connection
2. StatusResponse - response message of normal connection
3. Chunk - main message in streaming connection

# Util point of view:
For version 0.1.0-SNAPSHOT, it has 3 basic utilities: StorageManager, FileQuery and SystemLog (detail in Report).

## SystemLog

- route: "SystemLog"

### Action

1. countActivity
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{}"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: "{
            row_count: String
        }"

2. listActivity
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{}"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: Json String

3. listRepo
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{}"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: Json String

4. listUser
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{}"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: Json String

5. listCatalog
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{}"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: Json String

6. getUserInfo
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{
            name: String
        }"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: "{
            user_info: String
        }"

7. getCatalogInfo
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{
            name: String
        }"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: "{
            catalog_info: String
        }"

8. registerUser
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{
            name: String,
            describe: String
        }"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: ""

9. registerCatalog
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{
            name: String,
            describe: String
        }"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: ""

## StorageManager

- route: "StorageManager"

### Action

1. createRepo
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{
            path: String,
            schema: {
                fields: ["extra_field_1", "extra_field_2", v.v.],
                describe: String,
                type: String (FILE | DIR),
                status: String,
                notes: String
            }
        }"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: ""

2. updateRepo
    - Service: upload_file
    - State:
        0. define route:
            - request: String (route name)
            - response: "{
                action: {
                    code: String
                }
            }"
        1. setup paramter
            - request: "{
                action: String,
                parameter: {
                    repoId: String,
                    meta: {
                        user: String,
                        name: String (path name),
                        type: String (FILE/PATH),
                        format: String (exp: csv),
                        label: String (catalog),
                        extra_field_1: String,
                        extra_field_2: String,
                        v.v.
                    }
                }
            }"
            - response: "{
                action: {
                    code: String
                }
            }"
        2. upload file raw data in chunk (512 kb)

## FileQuery

- route: "FileQuery"

### Action

1. searchStorage
    - Service: setup
    - ActionRequest:
        - jsonAction: String
        - jsonParam: "{
            repoId: String,
            expr: "{
                path: String (option),
                select: ["field_1", "field_2", ...] (option),
                where: [
                    {
                        field: String,
                        operator: String ("=", ">", "<");
                        value: String
                    },
                    {
                        field: String,
                        operator: String ("=", ">", "<");
                        value: String
                    },
                    v.v.
                ] (option),
                order_by: {
                    field: String,
                    value: String ["ASC", "DESC"]
                } (option),
                limit: String (option)
            }"
        }"
    - StatusResponse:
        - system: "{
            error: []
        }"
        - action: "{
            status: String,
            message: String,
            code: String
        }"
        - result: Json String

2. loadData
    - Service: download_file
    - State:
        0. define route:
            - request: String (route name)
            - response: "{
                action: {
                    code: String
                }
            }"
        1. setup paramter
            - request: "{
                action: String,
                parameter: {
                    path: String (path of file)
                }
            }"
            - response: "{
                action: {
                    code: String
                }
            }"
        2. download file
            - request: String (200 - READY)
            - response: stream file raw data in chunk (512 kb)

(2020-09-22 | WARNNING: this doc ver was not built from release ver -> it is not completed)