package com.usth.hieplnc.api.grpc.basic;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 * <pre>
 **
 * List of service
 * NOTES:
 *  - The setup method is 1 state calling -&gt; therefore it
 *  can be easy to implement to gRPC
 *  - However, the file streaming is multiple state action
 *  hard to perform in gRPC service since gRPC calling is
 *  stateless. Therefore, recommandation will be using
 *  bi-direction streaming of gRPC and sending state as the
 *  bytes data -&gt; need extra parser to convert raw data back
 *  to request and response.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: basic_api.proto")
public final class StorageManagerGrpc {

  private StorageManagerGrpc() {}

  public static final String SERVICE_NAME = "com.usth.hieplnc.api.grpc.basic.StorageManager";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.ActionRequest,
      com.usth.hieplnc.api.grpc.basic.StatusResponse> METHOD_SETUP =
      io.grpc.MethodDescriptor.<com.usth.hieplnc.api.grpc.basic.ActionRequest, com.usth.hieplnc.api.grpc.basic.StatusResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.usth.hieplnc.api.grpc.basic.StorageManager", "setup"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.usth.hieplnc.api.grpc.basic.ActionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.usth.hieplnc.api.grpc.basic.StatusResponse.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk,
      com.usth.hieplnc.api.grpc.basic.Chunk> METHOD_UPLOAD_FILE =
      io.grpc.MethodDescriptor.<com.usth.hieplnc.api.grpc.basic.Chunk, com.usth.hieplnc.api.grpc.basic.Chunk>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "com.usth.hieplnc.api.grpc.basic.StorageManager", "upload_file"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk,
      com.usth.hieplnc.api.grpc.basic.Chunk> METHOD_DOWNLOAD_FILE =
      io.grpc.MethodDescriptor.<com.usth.hieplnc.api.grpc.basic.Chunk, com.usth.hieplnc.api.grpc.basic.Chunk>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "com.usth.hieplnc.api.grpc.basic.StorageManager", "download_file"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StorageManagerStub newStub(io.grpc.Channel channel) {
    return new StorageManagerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StorageManagerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StorageManagerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StorageManagerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StorageManagerFutureStub(channel);
  }

  /**
   * <pre>
   **
   * List of service
   * NOTES:
   *  - The setup method is 1 state calling -&gt; therefore it
   *  can be easy to implement to gRPC
   *  - However, the file streaming is multiple state action
   *  hard to perform in gRPC service since gRPC calling is
   *  stateless. Therefore, recommandation will be using
   *  bi-direction streaming of gRPC and sending state as the
   *  bytes data -&gt; need extra parser to convert raw data back
   *  to request and response.
   * </pre>
   */
  public static abstract class StorageManagerImplBase implements io.grpc.BindableService {

    /**
     */
    public void setup(com.usth.hieplnc.api.grpc.basic.ActionRequest request,
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.StatusResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SETUP, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> uploadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_UPLOAD_FILE, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> downloadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_DOWNLOAD_FILE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_SETUP,
            asyncUnaryCall(
              new MethodHandlers<
                com.usth.hieplnc.api.grpc.basic.ActionRequest,
                com.usth.hieplnc.api.grpc.basic.StatusResponse>(
                  this, METHODID_SETUP)))
          .addMethod(
            METHOD_UPLOAD_FILE,
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.usth.hieplnc.api.grpc.basic.Chunk,
                com.usth.hieplnc.api.grpc.basic.Chunk>(
                  this, METHODID_UPLOAD_FILE)))
          .addMethod(
            METHOD_DOWNLOAD_FILE,
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.usth.hieplnc.api.grpc.basic.Chunk,
                com.usth.hieplnc.api.grpc.basic.Chunk>(
                  this, METHODID_DOWNLOAD_FILE)))
          .build();
    }
  }

  /**
   * <pre>
   **
   * List of service
   * NOTES:
   *  - The setup method is 1 state calling -&gt; therefore it
   *  can be easy to implement to gRPC
   *  - However, the file streaming is multiple state action
   *  hard to perform in gRPC service since gRPC calling is
   *  stateless. Therefore, recommandation will be using
   *  bi-direction streaming of gRPC and sending state as the
   *  bytes data -&gt; need extra parser to convert raw data back
   *  to request and response.
   * </pre>
   */
  public static final class StorageManagerStub extends io.grpc.stub.AbstractStub<StorageManagerStub> {
    private StorageManagerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StorageManagerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageManagerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StorageManagerStub(channel, callOptions);
    }

    /**
     */
    public void setup(com.usth.hieplnc.api.grpc.basic.ActionRequest request,
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.StatusResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SETUP, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> uploadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(METHOD_UPLOAD_FILE, getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> downloadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(METHOD_DOWNLOAD_FILE, getCallOptions()), responseObserver);
    }
  }

  /**
   * <pre>
   **
   * List of service
   * NOTES:
   *  - The setup method is 1 state calling -&gt; therefore it
   *  can be easy to implement to gRPC
   *  - However, the file streaming is multiple state action
   *  hard to perform in gRPC service since gRPC calling is
   *  stateless. Therefore, recommandation will be using
   *  bi-direction streaming of gRPC and sending state as the
   *  bytes data -&gt; need extra parser to convert raw data back
   *  to request and response.
   * </pre>
   */
  public static final class StorageManagerBlockingStub extends io.grpc.stub.AbstractStub<StorageManagerBlockingStub> {
    private StorageManagerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StorageManagerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageManagerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StorageManagerBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.usth.hieplnc.api.grpc.basic.StatusResponse setup(com.usth.hieplnc.api.grpc.basic.ActionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SETUP, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   **
   * List of service
   * NOTES:
   *  - The setup method is 1 state calling -&gt; therefore it
   *  can be easy to implement to gRPC
   *  - However, the file streaming is multiple state action
   *  hard to perform in gRPC service since gRPC calling is
   *  stateless. Therefore, recommandation will be using
   *  bi-direction streaming of gRPC and sending state as the
   *  bytes data -&gt; need extra parser to convert raw data back
   *  to request and response.
   * </pre>
   */
  public static final class StorageManagerFutureStub extends io.grpc.stub.AbstractStub<StorageManagerFutureStub> {
    private StorageManagerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StorageManagerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageManagerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StorageManagerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.usth.hieplnc.api.grpc.basic.StatusResponse> setup(
        com.usth.hieplnc.api.grpc.basic.ActionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SETUP, getCallOptions()), request);
    }
  }

  private static final int METHODID_SETUP = 0;
  private static final int METHODID_UPLOAD_FILE = 1;
  private static final int METHODID_DOWNLOAD_FILE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final StorageManagerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(StorageManagerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SETUP:
          serviceImpl.setup((com.usth.hieplnc.api.grpc.basic.ActionRequest) request,
              (io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.StatusResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_UPLOAD_FILE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.uploadFile(
              (io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk>) responseObserver);
        case METHODID_DOWNLOAD_FILE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.downloadFile(
              (io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class StorageManagerDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.usth.hieplnc.api.grpc.basic.BasicApi.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StorageManagerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StorageManagerDescriptorSupplier())
              .addMethod(METHOD_SETUP)
              .addMethod(METHOD_UPLOAD_FILE)
              .addMethod(METHOD_DOWNLOAD_FILE)
              .build();
        }
      }
    }
    return result;
  }
}
