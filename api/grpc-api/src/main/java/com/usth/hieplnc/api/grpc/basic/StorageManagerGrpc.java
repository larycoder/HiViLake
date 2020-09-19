package com.usth.hieplnc.api.grpc.basic;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

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
    value = "by gRPC proto compiler (version 1.32.1)",
    comments = "Source: basic_api.proto")
public final class StorageManagerGrpc {

  private StorageManagerGrpc() {}

  public static final String SERVICE_NAME = "com.usth.hieplnc.api.grpc.basic.StorageManager";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.ActionRequest,
      com.usth.hieplnc.api.grpc.basic.StatusResponse> getSetupMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "setup",
      requestType = com.usth.hieplnc.api.grpc.basic.ActionRequest.class,
      responseType = com.usth.hieplnc.api.grpc.basic.StatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.ActionRequest,
      com.usth.hieplnc.api.grpc.basic.StatusResponse> getSetupMethod() {
    io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.ActionRequest, com.usth.hieplnc.api.grpc.basic.StatusResponse> getSetupMethod;
    if ((getSetupMethod = StorageManagerGrpc.getSetupMethod) == null) {
      synchronized (StorageManagerGrpc.class) {
        if ((getSetupMethod = StorageManagerGrpc.getSetupMethod) == null) {
          StorageManagerGrpc.getSetupMethod = getSetupMethod =
              io.grpc.MethodDescriptor.<com.usth.hieplnc.api.grpc.basic.ActionRequest, com.usth.hieplnc.api.grpc.basic.StatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "setup"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.usth.hieplnc.api.grpc.basic.ActionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.usth.hieplnc.api.grpc.basic.StatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StorageManagerMethodDescriptorSupplier("setup"))
              .build();
        }
      }
    }
    return getSetupMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk,
      com.usth.hieplnc.api.grpc.basic.Chunk> getUploadFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "upload_file",
      requestType = com.usth.hieplnc.api.grpc.basic.Chunk.class,
      responseType = com.usth.hieplnc.api.grpc.basic.Chunk.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk,
      com.usth.hieplnc.api.grpc.basic.Chunk> getUploadFileMethod() {
    io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk, com.usth.hieplnc.api.grpc.basic.Chunk> getUploadFileMethod;
    if ((getUploadFileMethod = StorageManagerGrpc.getUploadFileMethod) == null) {
      synchronized (StorageManagerGrpc.class) {
        if ((getUploadFileMethod = StorageManagerGrpc.getUploadFileMethod) == null) {
          StorageManagerGrpc.getUploadFileMethod = getUploadFileMethod =
              io.grpc.MethodDescriptor.<com.usth.hieplnc.api.grpc.basic.Chunk, com.usth.hieplnc.api.grpc.basic.Chunk>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "upload_file"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
              .setSchemaDescriptor(new StorageManagerMethodDescriptorSupplier("upload_file"))
              .build();
        }
      }
    }
    return getUploadFileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk,
      com.usth.hieplnc.api.grpc.basic.Chunk> getDownloadFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "download_file",
      requestType = com.usth.hieplnc.api.grpc.basic.Chunk.class,
      responseType = com.usth.hieplnc.api.grpc.basic.Chunk.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk,
      com.usth.hieplnc.api.grpc.basic.Chunk> getDownloadFileMethod() {
    io.grpc.MethodDescriptor<com.usth.hieplnc.api.grpc.basic.Chunk, com.usth.hieplnc.api.grpc.basic.Chunk> getDownloadFileMethod;
    if ((getDownloadFileMethod = StorageManagerGrpc.getDownloadFileMethod) == null) {
      synchronized (StorageManagerGrpc.class) {
        if ((getDownloadFileMethod = StorageManagerGrpc.getDownloadFileMethod) == null) {
          StorageManagerGrpc.getDownloadFileMethod = getDownloadFileMethod =
              io.grpc.MethodDescriptor.<com.usth.hieplnc.api.grpc.basic.Chunk, com.usth.hieplnc.api.grpc.basic.Chunk>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "download_file"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.usth.hieplnc.api.grpc.basic.Chunk.getDefaultInstance()))
              .setSchemaDescriptor(new StorageManagerMethodDescriptorSupplier("download_file"))
              .build();
        }
      }
    }
    return getDownloadFileMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StorageManagerStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageManagerStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StorageManagerStub>() {
        @java.lang.Override
        public StorageManagerStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StorageManagerStub(channel, callOptions);
        }
      };
    return StorageManagerStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StorageManagerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageManagerBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StorageManagerBlockingStub>() {
        @java.lang.Override
        public StorageManagerBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StorageManagerBlockingStub(channel, callOptions);
        }
      };
    return StorageManagerBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StorageManagerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StorageManagerFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StorageManagerFutureStub>() {
        @java.lang.Override
        public StorageManagerFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StorageManagerFutureStub(channel, callOptions);
        }
      };
    return StorageManagerFutureStub.newStub(factory, channel);
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
      asyncUnimplementedUnaryCall(getSetupMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> uploadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncUnimplementedStreamingCall(getUploadFileMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> downloadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncUnimplementedStreamingCall(getDownloadFileMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSetupMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.usth.hieplnc.api.grpc.basic.ActionRequest,
                com.usth.hieplnc.api.grpc.basic.StatusResponse>(
                  this, METHODID_SETUP)))
          .addMethod(
            getUploadFileMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.usth.hieplnc.api.grpc.basic.Chunk,
                com.usth.hieplnc.api.grpc.basic.Chunk>(
                  this, METHODID_UPLOAD_FILE)))
          .addMethod(
            getDownloadFileMethod(),
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
  public static final class StorageManagerStub extends io.grpc.stub.AbstractAsyncStub<StorageManagerStub> {
    private StorageManagerStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageManagerStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageManagerStub(channel, callOptions);
    }

    /**
     */
    public void setup(com.usth.hieplnc.api.grpc.basic.ActionRequest request,
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.StatusResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetupMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> uploadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getUploadFileMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> downloadFile(
        io.grpc.stub.StreamObserver<com.usth.hieplnc.api.grpc.basic.Chunk> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getDownloadFileMethod(), getCallOptions()), responseObserver);
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
  public static final class StorageManagerBlockingStub extends io.grpc.stub.AbstractBlockingStub<StorageManagerBlockingStub> {
    private StorageManagerBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageManagerBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageManagerBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.usth.hieplnc.api.grpc.basic.StatusResponse setup(com.usth.hieplnc.api.grpc.basic.ActionRequest request) {
      return blockingUnaryCall(
          getChannel(), getSetupMethod(), getCallOptions(), request);
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
  public static final class StorageManagerFutureStub extends io.grpc.stub.AbstractFutureStub<StorageManagerFutureStub> {
    private StorageManagerFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StorageManagerFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StorageManagerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.usth.hieplnc.api.grpc.basic.StatusResponse> setup(
        com.usth.hieplnc.api.grpc.basic.ActionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getSetupMethod(), getCallOptions()), request);
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

  private static abstract class StorageManagerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StorageManagerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.usth.hieplnc.api.grpc.basic.BasicApi.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StorageManager");
    }
  }

  private static final class StorageManagerFileDescriptorSupplier
      extends StorageManagerBaseDescriptorSupplier {
    StorageManagerFileDescriptorSupplier() {}
  }

  private static final class StorageManagerMethodDescriptorSupplier
      extends StorageManagerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    StorageManagerMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
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
              .setSchemaDescriptor(new StorageManagerFileDescriptorSupplier())
              .addMethod(getSetupMethod())
              .addMethod(getUploadFileMethod())
              .addMethod(getDownloadFileMethod())
              .build();
        }
      }
    }
    return result;
  }
}
