package com.usth.hieplnc.api.grpc.basic;

/**
 * DOC:
 * - Server wrapper class to handle gRPC connection
 *
 */

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.ServerCallStreamObserver;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.protobuf.ByteString;

import com.usth.hieplnc.util.api.Service;
import com.usth.hieplnc.api.secondapi.Registor;

public class StorageManagerServer implements Registor{
// variable
    private static HashMap<String, Service> route = new HashMap<String, Service>();

    private static final Logger logger = Logger.getLogger(StorageManagerServer.class.getName());
    private Server server;
    private int port;

//========================================================//
// constructor
    public StorageManagerServer(int port) throws IOException{
        this.port = port;
    }

//========================================================//
// method
    public void start() throws IOException{
        this.server = ServerBuilder.forPort(this.port)
                        .addService(new StorageManagerImpl())
                        .build()
                        .start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try{
                    StorageManagerServer.this.stop();
                } catch(InterruptedException e){
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    public void stop() throws InterruptedException{
        if(this.server != null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException{
        if(this.server != null){
            server.awaitTermination();
        }
    }

    @Override
    public void register(Service service, JSONObject api){
        this.route.put((String) api.get("route"), service);
        logger.info("Routor get new route: " + (String) api.get("route"));
    }

//========================================================//
// inner class
    static class StorageManagerImpl extends StorageManagerGrpc.StorageManagerImplBase{
        @Override
        public void setup(ActionRequest request, StreamObserver<StatusResponse> responseObserver){
            // get service
            Service service = route.get(request.getRoute()).duplicate();
            
            // build request
            JSONObject utilRequest = new JSONObject();

            // get action
            utilRequest.put("action", request.getJsonAction());

            // get parameter
            JSONParser jsonParser = new JSONParser();
            try{
                utilRequest.put("parameter", jsonParser.parse(request.getJsonParam()));
            } catch(ParseException e){
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "request format error");
                errorResponse.put("message", e.toString());
                errorResponse.put("code", "4");
                StatusResponse errorReply = StatusResponse.newBuilder()
                                                .setSystem("{}")
                                                .setAction(errorResponse.toString())
                                                .setResult("{}")
                                                .build();
                responseObserver.onNext(errorReply);
                responseObserver.onCompleted();
                return;
            }
            
            // set parameter
            if(service.setParameter(utilRequest)){
                JSONObject serviceJsonReply = service.getStatus();
                StatusResponse serviceReply = StatusResponse.newBuilder()
                                                    .setSystem(serviceJsonReply.get("system").toString())
                                                    .setAction(serviceJsonReply.get("action").toString())
                                                    .setResult(serviceJsonReply.get("result").toString())
                                                    .build();
                responseObserver.onNext(serviceReply);
                responseObserver.onCompleted();
            } else{
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "request format error");
                errorResponse.put("message", "service in route " + request.getRoute() + " could not parse request");
                errorResponse.put("code", "4");
                StatusResponse errorReply = StatusResponse.newBuilder()
                                                .setSystem("{}")
                                                .setAction(errorResponse.toString())
                                                .setResult("{}")
                                                .build();
                responseObserver.onNext(errorReply);
                responseObserver.onCompleted();
            }
        }

        @Override
        public StreamObserver<Chunk> uploadFile(final StreamObserver<Chunk> responseObserver){
            final ServerCallStreamObserver<Chunk> serverCallStreamObserver = 
                (ServerCallStreamObserver<Chunk>) responseObserver;
            serverCallStreamObserver.disableAutoRequest();

            class OnReadyHandler implements Runnable{
                private boolean wasReady = false;

                @Override
                public void run(){
                    if(serverCallStreamObserver.isReady() && !wasReady){
                        wasReady = true;
                        serverCallStreamObserver.request(1);
                    }
                }
            }
            final OnReadyHandler onReadyHandler = new OnReadyHandler();
            serverCallStreamObserver.setOnReadyHandler(onReadyHandler);

            return new StreamObserver<Chunk>(){
                private int serviceState = 0; // -1 is dead state where client can not request anything
                private Service service;
                private OutputStream uploadStream;

                @Override
                public void onNext(Chunk request){
                    try{
                        ByteString rawData = request.getData();

                        // state action
                        if(serviceState == 0){
                            // define route
                            Service serviceTemp = route.get(rawData.toString());
                            if(serviceTemp == null){
                                throw new Exception("Could not find out the route");
                            }
                            this.service = serviceTemp.duplicate();
                            serviceState++;
                            
                            JSONObject confirm = new JSONObject();
                            confirm.put("system", "");
                            confirm.put("result", "");
                            JSONObject statusConfirm = new JSONObject();
                            statusConfirm.put("code", "200");
                            confirm.put("action", statusConfirm);
                            InputStream streamConfirm = new ByteArrayInputStream(confirm.toString().getBytes());
                            responseObserver.onNext(Chunk.newBuilder().setData(ByteString.readFrom(streamConfirm)).build());
                        } else if(serviceState == 1){
                            // set parameter and open file writer
                            String action = rawData.toString();
                            JSONParser jsonParser = new JSONParser();
                            try{
                                JSONObject jsonAction = (JSONObject) jsonParser.parse(action);
                                if(this.service.setParameter(jsonAction)){
                                    this.uploadStream = this.service.pushFile();
                                    if(this.uploadStream != null){
                                        serviceState++;
                                        JSONObject confirm = new JSONObject();
                                        confirm.put("system", "");
                                        confirm.put("result", "");
                                        JSONObject statusConfirm = new JSONObject();
                                        statusConfirm.put("code", "200");
                                        confirm.put("action", statusConfirm);
                                        InputStream streamConfirm = new ByteArrayInputStream(confirm.toString().getBytes());
                                        responseObserver.onNext(Chunk.newBuilder().setData(ByteString.readFrom(streamConfirm)).build());
                                    } else{
                                        JSONObject error = new JSONObject();
                                        error.put("system", "");
                                        error.put("result", "");
                                        error.put("action", this.service.getStatus());
                                        InputStream streamConfirm = new ByteArrayInputStream(error.toString().getBytes());
                                        responseObserver.onNext(Chunk.newBuilder().setData(ByteString.readFrom(streamConfirm)).build());
                                        this.serviceState = -1;
                                        return;
                                    }
                                } else{
                                    JSONObject errorJson = new JSONObject();
                                    errorJson.put("system", new JSONObject());
                                    JSONObject errorActionJson = new JSONObject();
                                    errorActionJson.put("status", "upload file error");
                                    errorActionJson.put("message", "service could not parsing the action request");
                                    errorActionJson.put("code", "4");
                                    errorJson.put("action", errorActionJson);
                                    errorJson.put("result", new JSONObject());
                                    InputStream errorStream = new ByteArrayInputStream(errorJson.toString().getBytes());
                                    Chunk reply = Chunk.newBuilder().setData(ByteString.readFrom(errorStream)).build();
                                    responseObserver.onNext(reply);
                                    this.serviceState = -1;
                                    return;
                                }
                            } catch(ParseException e){
                                JSONObject errorJson = new JSONObject();
                                errorJson.put("system", new JSONObject());
                                JSONObject errorActionJson = new JSONObject();
                                errorActionJson.put("status", "upload file error");
                                errorActionJson.put("message", e.toString());
                                errorActionJson.put("code", "4");
                                errorJson.put("action", errorActionJson);
                                errorJson.put("result", new JSONObject());
                                InputStream errorStream = new ByteArrayInputStream(errorJson.toString().getBytes());
                                Chunk reply = Chunk.newBuilder().setData(ByteString.readFrom(errorStream)).build();
                                responseObserver.onNext(reply);
                                this.serviceState = -1;
                                return;
                            }
                        } else if(serviceState == 2){
                            // push data to server
                            try{
                                this.uploadStream.write(rawData.toByteArray());
                                this.uploadStream.flush();
                            } catch(Exception e){
                                JSONObject error = new JSONObject();
                                error.put("system", "");
                                error.put("result", "");
                                JSONObject actionError = new JSONObject();
                                actionError.put("status", "upload file error");
                                actionError.put("message", e.toString());
                                actionError.put("code", "4");
                                error.put("action", actionError);
                                ByteString reply = ByteString.copyFrom(error.toString().getBytes());
                                responseObserver.onNext(Chunk.newBuilder().setData(reply).build());
                                this.serviceState = -1;
                            }
                        }

                        if(serverCallStreamObserver.isReady()){
                            serverCallStreamObserver.request(1);
                        } else{
                            onReadyHandler.wasReady = false;
                        }
                    } catch(Throwable throwable){
                        throwable.printStackTrace();
                        responseObserver.onError(
                            Status.UNKNOWN.withDescription("Error handling request")
                                            .withCause(throwable)
                                            .asException());
                        this.serviceState = -1;
                    }
                }

                @Override
                public void onError(Throwable t){
                    t.printStackTrace();
                    // close file
                    if(this.uploadStream != null){
                        try{
                            this.uploadStream.flush();
                            this.uploadStream.close();
                        } catch(Exception e){
                            e.printStackTrace();
                            this.uploadStream = null;
                        }
                    }
                    this.serviceState = -1;
                    responseObserver.onCompleted();
                }

                @Override
                public void onCompleted(){
                    // close file
                    if(this.uploadStream != null){
                        try{
                            this.uploadStream.flush();
                            this.uploadStream.close();
                        } catch(Exception e){
                            e.printStackTrace();
                            this.uploadStream = null;
                        }
                    }
                    this.serviceState = -1;
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<Chunk> downloadFile(final StreamObserver<Chunk> responseObserver){
            final ServerCallStreamObserver<Chunk> serverCallStreamObserver = 
                (ServerCallStreamObserver<Chunk>) responseObserver;
            serverCallStreamObserver.disableAutoRequest();

            class OnReadyHandler implements Runnable{
                private boolean wasReady = false;

                @Override
                public void run(){
                    if(serverCallStreamObserver.isReady() && !wasReady){
                        wasReady = true;
                        serverCallStreamObserver.request(1);
                    }
                }
            }
            final OnReadyHandler onReadyHandler = new OnReadyHandler();
            serverCallStreamObserver.setOnReadyHandler(onReadyHandler);

            return new StreamObserver<Chunk>(){
                private int serviceState = 0; // -1 is dead state where client can not request anything
                private Service service;
                private InputStream downloadStream;
                private byte[] buffer = new byte[1024 * 512];

                @Override
                public void onNext(Chunk request){
                    try{
                        ByteString rawData = request.getData();

                        // state action
                        if(serviceState == 0){
                            // define route
                            Service serviceTemp = route.get(rawData.toString());
                            if(serviceTemp == null){
                                throw new Exception("Could not find out the route");
                            }
                            this.service = serviceTemp.duplicate();
                            serviceState++;

                            JSONObject confirm = new JSONObject();
                            confirm.put("system", "");
                            confirm.put("result", "");
                            JSONObject statusConfirm = new JSONObject();
                            statusConfirm.put("code", "200");
                            confirm.put("action", statusConfirm);
                            InputStream streamConfirm = new ByteArrayInputStream(confirm.toString().getBytes());
                            responseObserver.onNext(Chunk.newBuilder().setData(ByteString.readFrom(streamConfirm)).build());
                        } else if(serviceState == 1){
                            // set parameter and open file reader
                            String action = rawData.toString();
                            JSONParser jsonParser = new JSONParser();
                            try{
                                JSONObject jsonAction = (JSONObject) jsonParser.parse(action);
                                if(this.service.setParameter(jsonAction)){
                                    this.downloadStream = this.service.pullFile();
                                    if(this.downloadStream != null){
                                        serviceState++;
                                        JSONObject confirm = new JSONObject();
                                        confirm.put("system", "");
                                        confirm.put("result", "");
                                        JSONObject statusConfirm = new JSONObject();
                                        statusConfirm.put("code", "200");
                                        confirm.put("action", statusConfirm);
                                        InputStream streamConfirm = new ByteArrayInputStream(confirm.toString().getBytes());
                                        responseObserver.onNext(Chunk.newBuilder().setData(ByteString.readFrom(streamConfirm)).build());
                                    } else{
                                        JSONObject error = new JSONObject();
                                        error.put("system", "");
                                        error.put("result", "");
                                        error.put("action", this.service.getStatus());
                                        InputStream streamConfirm = new ByteArrayInputStream(error.toString().getBytes());
                                        responseObserver.onNext(Chunk.newBuilder().setData(ByteString.readFrom(streamConfirm)).build());
                                        this.serviceState = -1;
                                        return;
                                    }
                                } else{
                                    JSONObject errorJson = new JSONObject();
                                    errorJson.put("system", new JSONObject());
                                    JSONObject errorActionJson = new JSONObject();
                                    errorActionJson.put("status", "download file error");
                                    errorActionJson.put("message", "service could not parsing the action request");
                                    errorActionJson.put("code", "4");
                                    errorJson.put("action", errorActionJson);
                                    errorJson.put("result", new JSONObject());
                                    InputStream errorStream = new ByteArrayInputStream(errorJson.toString().getBytes());
                                    Chunk reply = Chunk.newBuilder().setData(ByteString.readFrom(errorStream)).build();
                                    responseObserver.onNext(reply);
                                    this.serviceState = -1;
                                    return;
                                }
                            } catch(ParseException e){
                                JSONObject errorJson = new JSONObject();
                                errorJson.put("system", new JSONObject());
                                JSONObject errorActionJson = new JSONObject();
                                errorActionJson.put("status", "download file error");
                                errorActionJson.put("message", e.toString());
                                errorActionJson.put("code", "4");
                                errorJson.put("action", errorActionJson);
                                errorJson.put("result", new JSONObject());
                                InputStream errorStream = new ByteArrayInputStream(errorJson.toString().getBytes());
                                Chunk reply = Chunk.newBuilder().setData(ByteString.readFrom(errorStream)).build();
                                responseObserver.onNext(reply);
                                this.serviceState = -1;
                                return;
                            }
                        } else if(this.serviceState == 2){
                            if(!rawData.toString().equals("200")){
                                this.serviceState = -1;
                                return;
                            }

                            // download data from server
                            try{
                                // read 512kb from file
                                int len = this.downloadStream.read(this.buffer);
                                responseObserver.onNext(Chunk.newBuilder()
                                                            .setData(ByteString.copyFrom(this.buffer, 0, len))
                                                            .build());
                                
                                if(len <= 0){
                                    this.serviceState = -1;
                                    return;
                                }
                            } catch(Exception e){
                                JSONObject error = new JSONObject();
                                error.put("system", "");
                                error.put("result", "");
                                JSONObject actionError = new JSONObject();
                                actionError.put("status", "download file error");
                                actionError.put("message", e.toString());
                                actionError.put("code", "4");
                                error.put("action", actionError);
                                // ByteString reply = ByteString.copyFrom(error.toString().getBytes());
                                // responseObserver.onNext(Chunk.newBuilder().setData(reply).build());
                                this.serviceState = -1;
                                return;
                            }
                        }
                        
                        if(serverCallStreamObserver.isReady()){
                            serverCallStreamObserver.request(1);
                        } else{
                            onReadyHandler.wasReady = false;
                        }
                    } catch(Throwable throwable){
                        throwable.printStackTrace();
                        responseObserver.onError(
                            Status.UNKNOWN.withDescription("Error handling request")
                                            .withCause(throwable)
                                            .asException());
                        this.serviceState = -1;
                    }
                }

                @Override
                public void onError(Throwable t){
                    t.printStackTrace();
                    // close file
                    if(this.downloadStream != null){
                        try{
                            this.downloadStream.close();
                        } catch(Exception e){
                            e.printStackTrace();
                            this.downloadStream = null;
                        }
                    }
                    this.serviceState = -1;
                    responseObserver.onCompleted();
                }

                @Override
                public void onCompleted(){
                    // close file
                    if(this.downloadStream != null){
                        try{
                            this.downloadStream.close();
                        } catch(Exception e){
                            e.printStackTrace();
                            this.downloadStream = null;
                        }
                    }
                    this.serviceState = -1;
                    responseObserver.onCompleted();
                }
            };
        }
    }

}