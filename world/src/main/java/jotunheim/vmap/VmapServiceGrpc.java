package jotunheim.vmap;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: internal_vmap.proto")
public final class VmapServiceGrpc {

  private VmapServiceGrpc() {}

  public static final String SERVICE_NAME = "jotunheim.vmap.VmapService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<InternalVmap.GetPathRequest,
      InternalVmap.GetPathResponse> getGetPathMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetPath",
      requestType = InternalVmap.GetPathRequest.class,
      responseType = InternalVmap.GetPathResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<InternalVmap.GetPathRequest,
      InternalVmap.GetPathResponse> getGetPathMethod() {
    io.grpc.MethodDescriptor<InternalVmap.GetPathRequest, InternalVmap.GetPathResponse> getGetPathMethod;
    if ((getGetPathMethod = VmapServiceGrpc.getGetPathMethod) == null) {
      synchronized (VmapServiceGrpc.class) {
        if ((getGetPathMethod = VmapServiceGrpc.getGetPathMethod) == null) {
          VmapServiceGrpc.getGetPathMethod = getGetPathMethod =
              io.grpc.MethodDescriptor.<InternalVmap.GetPathRequest, InternalVmap.GetPathResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetPath"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InternalVmap.GetPathRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InternalVmap.GetPathResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VmapServiceMethodDescriptorSupplier("GetPath"))
              .build();
        }
      }
    }
    return getGetPathMethod;
  }

  private static volatile io.grpc.MethodDescriptor<InternalVmap.CheckLOSRequest,
      InternalVmap.CheckLOSResponse> getCheckLOSMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CheckLOS",
      requestType = InternalVmap.CheckLOSRequest.class,
      responseType = InternalVmap.CheckLOSResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<InternalVmap.CheckLOSRequest,
      InternalVmap.CheckLOSResponse> getCheckLOSMethod() {
    io.grpc.MethodDescriptor<InternalVmap.CheckLOSRequest, InternalVmap.CheckLOSResponse> getCheckLOSMethod;
    if ((getCheckLOSMethod = VmapServiceGrpc.getCheckLOSMethod) == null) {
      synchronized (VmapServiceGrpc.class) {
        if ((getCheckLOSMethod = VmapServiceGrpc.getCheckLOSMethod) == null) {
          VmapServiceGrpc.getCheckLOSMethod = getCheckLOSMethod =
              io.grpc.MethodDescriptor.<InternalVmap.CheckLOSRequest, InternalVmap.CheckLOSResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CheckLOS"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InternalVmap.CheckLOSRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InternalVmap.CheckLOSResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VmapServiceMethodDescriptorSupplier("CheckLOS"))
              .build();
        }
      }
    }
    return getCheckLOSMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VmapServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VmapServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VmapServiceStub>() {
        @Override
        public VmapServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VmapServiceStub(channel, callOptions);
        }
      };
    return VmapServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VmapServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VmapServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VmapServiceBlockingStub>() {
        @Override
        public VmapServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VmapServiceBlockingStub(channel, callOptions);
        }
      };
    return VmapServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VmapServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VmapServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VmapServiceFutureStub>() {
        @Override
        public VmapServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VmapServiceFutureStub(channel, callOptions);
        }
      };
    return VmapServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class VmapServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getPath(InternalVmap.GetPathRequest request,
                        io.grpc.stub.StreamObserver<InternalVmap.GetPathResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetPathMethod(), responseObserver);
    }

    /**
     */
    public void checkLOS(InternalVmap.CheckLOSRequest request,
                         io.grpc.stub.StreamObserver<InternalVmap.CheckLOSResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCheckLOSMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetPathMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                InternalVmap.GetPathRequest,
                InternalVmap.GetPathResponse>(
                  this, METHODID_GET_PATH)))
          .addMethod(
            getCheckLOSMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                InternalVmap.CheckLOSRequest,
                InternalVmap.CheckLOSResponse>(
                  this, METHODID_CHECK_LOS)))
          .build();
    }
  }

  /**
   */
  public static final class VmapServiceStub extends io.grpc.stub.AbstractAsyncStub<VmapServiceStub> {
    private VmapServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected VmapServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VmapServiceStub(channel, callOptions);
    }

    /**
     */
    public void getPath(InternalVmap.GetPathRequest request,
                        io.grpc.stub.StreamObserver<InternalVmap.GetPathResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetPathMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void checkLOS(InternalVmap.CheckLOSRequest request,
                         io.grpc.stub.StreamObserver<InternalVmap.CheckLOSResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCheckLOSMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class VmapServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<VmapServiceBlockingStub> {
    private VmapServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected VmapServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VmapServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public InternalVmap.GetPathResponse getPath(InternalVmap.GetPathRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetPathMethod(), getCallOptions(), request);
    }

    /**
     */
    public InternalVmap.CheckLOSResponse checkLOS(InternalVmap.CheckLOSRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCheckLOSMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class VmapServiceFutureStub extends io.grpc.stub.AbstractFutureStub<VmapServiceFutureStub> {
    private VmapServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected VmapServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VmapServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<InternalVmap.GetPathResponse> getPath(
        InternalVmap.GetPathRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetPathMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<InternalVmap.CheckLOSResponse> checkLOS(
        InternalVmap.CheckLOSRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCheckLOSMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PATH = 0;
  private static final int METHODID_CHECK_LOS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final VmapServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(VmapServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PATH:
          serviceImpl.getPath((InternalVmap.GetPathRequest) request,
              (io.grpc.stub.StreamObserver<InternalVmap.GetPathResponse>) responseObserver);
          break;
        case METHODID_CHECK_LOS:
          serviceImpl.checkLOS((InternalVmap.CheckLOSRequest) request,
              (io.grpc.stub.StreamObserver<InternalVmap.CheckLOSResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class VmapServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VmapServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return InternalVmap.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VmapService");
    }
  }

  private static final class VmapServiceFileDescriptorSupplier
      extends VmapServiceBaseDescriptorSupplier {
    VmapServiceFileDescriptorSupplier() {}
  }

  private static final class VmapServiceMethodDescriptorSupplier
      extends VmapServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    VmapServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (VmapServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VmapServiceFileDescriptorSupplier())
              .addMethod(getGetPathMethod())
              .addMethod(getCheckLOSMethod())
              .build();
        }
      }
    }
    return result;
  }
}
