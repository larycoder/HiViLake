package com.usth.hieplnc.api.grpc;

/**
 * DOC:
 * - This class is main applicatio for run gRPC
 *
 */

import org.json.simple.JSONObject;

import com.usth.hieplnc.storage.api.*;
import com.usth.hieplnc.storage.hadoop.StorageFactory;

import com.usth.hieplnc.util.api.*;
import com.usth.hieplnc.util.base.systemlog.*;
import com.usth.hieplnc.util.base.filequery.*;
import com.usth.hieplnc.util.base.storagemanager.*;


import com.usth.hieplnc.api.grpc.basic.StorageManagerServer;

public class Application{
    public static void main(String[] args) throws Exception{
        // init storage
        StorageFactory factory = new StorageFactory();
        StorageWrapper storage = factory.setParam("fs.defaultFS", "hdfs://localhost:9000")
                                            .newInstance();

        // init util
        Service systemLogService = new SystemLog(storage, "/demo/system_log");
        Service storageManagerService = new StorageManager(storage, (SystemLog) systemLogService);
        Service fileQuerService = new FileQuery(storage, (SystemLog) systemLogService);

        // init server
        StorageManagerServer app = new StorageManagerServer(50050);

        // add api route
        // system log
        JSONObject api = new JSONObject();
        api.put("route", "SystemLog");
        app.register(systemLogService, api);

        // storage manager
        api = new JSONObject();
        api.put("route", "StorageManager");
        app.register(storageManagerService, api);

        // file query
        api = new JSONObject();
        api.put("route", "FileQuery");
        app.register(fileQuerService, api);

        // run server
        app.start();
        app.blockUntilShutdown();

        // close storage
        storage.close();
    }
}