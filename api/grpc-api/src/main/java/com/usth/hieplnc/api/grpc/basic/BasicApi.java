// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: basic_api.proto

package com.usth.hieplnc.api.grpc.basic;

public final class BasicApi {
  private BasicApi() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_usth_hieplnc_api_grpc_basic_ActionRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_usth_hieplnc_api_grpc_basic_ActionRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_usth_hieplnc_api_grpc_basic_StatusResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_usth_hieplnc_api_grpc_basic_StatusResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_usth_hieplnc_api_grpc_basic_Chunk_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_usth_hieplnc_api_grpc_basic_Chunk_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017basic_api.proto\022\037com.usth.hieplnc.api." +
      "grpc.basic\"E\n\rActionRequest\022\r\n\005route\030\001 \001" +
      "(\t\022\022\n\njsonAction\030\002 \001(\t\022\021\n\tjsonParam\030\003 \001(" +
      "\t\"@\n\016StatusResponse\022\016\n\006system\030\001 \001(\t\022\016\n\006a" +
      "ction\030\002 \001(\t\022\016\n\006result\030\003 \001(\t\"\025\n\005Chunk\022\014\n\004" +
      "data\030\001 \001(\0142\310\002\n\016StorageManager\022j\n\005setup\022." +
      ".com.usth.hieplnc.api.grpc.basic.ActionR" +
      "equest\032/.com.usth.hieplnc.api.grpc.basic" +
      ".StatusResponse\"\000\022c\n\013upload_file\022&.com.u" +
      "sth.hieplnc.api.grpc.basic.Chunk\032&.com.u" +
      "sth.hieplnc.api.grpc.basic.Chunk\"\000(\0010\001\022e" +
      "\n\rdownload_file\022&.com.usth.hieplnc.api.g" +
      "rpc.basic.Chunk\032&.com.usth.hieplnc.api.g" +
      "rpc.basic.Chunk\"\000(\0010\001B#\n\037com.usth.hiepln" +
      "c.api.grpc.basicP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_usth_hieplnc_api_grpc_basic_ActionRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_usth_hieplnc_api_grpc_basic_ActionRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_usth_hieplnc_api_grpc_basic_ActionRequest_descriptor,
        new java.lang.String[] { "Route", "JsonAction", "JsonParam", });
    internal_static_com_usth_hieplnc_api_grpc_basic_StatusResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_usth_hieplnc_api_grpc_basic_StatusResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_usth_hieplnc_api_grpc_basic_StatusResponse_descriptor,
        new java.lang.String[] { "System", "Action", "Result", });
    internal_static_com_usth_hieplnc_api_grpc_basic_Chunk_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_com_usth_hieplnc_api_grpc_basic_Chunk_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_usth_hieplnc_api_grpc_basic_Chunk_descriptor,
        new java.lang.String[] { "Data", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}