package io.liftbridge.proto;

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
 * API is the main Liftbridge server interface clients interact with.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.26.0)",
    comments = "Source: liftbridge-api/api.proto")
public final class APIGrpc {

  private APIGrpc() {}

  public static final String SERVICE_NAME = "proto.API";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.liftbridge.proto.Api.CreateStreamRequest,
      io.liftbridge.proto.Api.CreateStreamResponse> getCreateStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateStream",
      requestType = io.liftbridge.proto.Api.CreateStreamRequest.class,
      responseType = io.liftbridge.proto.Api.CreateStreamResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.liftbridge.proto.Api.CreateStreamRequest,
      io.liftbridge.proto.Api.CreateStreamResponse> getCreateStreamMethod() {
    io.grpc.MethodDescriptor<io.liftbridge.proto.Api.CreateStreamRequest, io.liftbridge.proto.Api.CreateStreamResponse> getCreateStreamMethod;
    if ((getCreateStreamMethod = APIGrpc.getCreateStreamMethod) == null) {
      synchronized (APIGrpc.class) {
        if ((getCreateStreamMethod = APIGrpc.getCreateStreamMethod) == null) {
          APIGrpc.getCreateStreamMethod = getCreateStreamMethod =
              io.grpc.MethodDescriptor.<io.liftbridge.proto.Api.CreateStreamRequest, io.liftbridge.proto.Api.CreateStreamResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.CreateStreamRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.CreateStreamResponse.getDefaultInstance()))
              .setSchemaDescriptor(new APIMethodDescriptorSupplier("CreateStream"))
              .build();
        }
      }
    }
    return getCreateStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.liftbridge.proto.Api.SubscribeRequest,
      io.liftbridge.proto.Api.Message> getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = io.liftbridge.proto.Api.SubscribeRequest.class,
      responseType = io.liftbridge.proto.Api.Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<io.liftbridge.proto.Api.SubscribeRequest,
      io.liftbridge.proto.Api.Message> getSubscribeMethod() {
    io.grpc.MethodDescriptor<io.liftbridge.proto.Api.SubscribeRequest, io.liftbridge.proto.Api.Message> getSubscribeMethod;
    if ((getSubscribeMethod = APIGrpc.getSubscribeMethod) == null) {
      synchronized (APIGrpc.class) {
        if ((getSubscribeMethod = APIGrpc.getSubscribeMethod) == null) {
          APIGrpc.getSubscribeMethod = getSubscribeMethod =
              io.grpc.MethodDescriptor.<io.liftbridge.proto.Api.SubscribeRequest, io.liftbridge.proto.Api.Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.Message.getDefaultInstance()))
              .setSchemaDescriptor(new APIMethodDescriptorSupplier("Subscribe"))
              .build();
        }
      }
    }
    return getSubscribeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.liftbridge.proto.Api.FetchMetadataRequest,
      io.liftbridge.proto.Api.FetchMetadataResponse> getFetchMetadataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FetchMetadata",
      requestType = io.liftbridge.proto.Api.FetchMetadataRequest.class,
      responseType = io.liftbridge.proto.Api.FetchMetadataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.liftbridge.proto.Api.FetchMetadataRequest,
      io.liftbridge.proto.Api.FetchMetadataResponse> getFetchMetadataMethod() {
    io.grpc.MethodDescriptor<io.liftbridge.proto.Api.FetchMetadataRequest, io.liftbridge.proto.Api.FetchMetadataResponse> getFetchMetadataMethod;
    if ((getFetchMetadataMethod = APIGrpc.getFetchMetadataMethod) == null) {
      synchronized (APIGrpc.class) {
        if ((getFetchMetadataMethod = APIGrpc.getFetchMetadataMethod) == null) {
          APIGrpc.getFetchMetadataMethod = getFetchMetadataMethod =
              io.grpc.MethodDescriptor.<io.liftbridge.proto.Api.FetchMetadataRequest, io.liftbridge.proto.Api.FetchMetadataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FetchMetadata"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.FetchMetadataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.FetchMetadataResponse.getDefaultInstance()))
              .setSchemaDescriptor(new APIMethodDescriptorSupplier("FetchMetadata"))
              .build();
        }
      }
    }
    return getFetchMetadataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.liftbridge.proto.Api.PublishRequest,
      io.liftbridge.proto.Api.PublishResponse> getPublishMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Publish",
      requestType = io.liftbridge.proto.Api.PublishRequest.class,
      responseType = io.liftbridge.proto.Api.PublishResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.liftbridge.proto.Api.PublishRequest,
      io.liftbridge.proto.Api.PublishResponse> getPublishMethod() {
    io.grpc.MethodDescriptor<io.liftbridge.proto.Api.PublishRequest, io.liftbridge.proto.Api.PublishResponse> getPublishMethod;
    if ((getPublishMethod = APIGrpc.getPublishMethod) == null) {
      synchronized (APIGrpc.class) {
        if ((getPublishMethod = APIGrpc.getPublishMethod) == null) {
          APIGrpc.getPublishMethod = getPublishMethod =
              io.grpc.MethodDescriptor.<io.liftbridge.proto.Api.PublishRequest, io.liftbridge.proto.Api.PublishResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Publish"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.PublishRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.liftbridge.proto.Api.PublishResponse.getDefaultInstance()))
              .setSchemaDescriptor(new APIMethodDescriptorSupplier("Publish"))
              .build();
        }
      }
    }
    return getPublishMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static APIStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<APIStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<APIStub>() {
        @java.lang.Override
        public APIStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new APIStub(channel, callOptions);
        }
      };
    return APIStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static APIBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<APIBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<APIBlockingStub>() {
        @java.lang.Override
        public APIBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new APIBlockingStub(channel, callOptions);
        }
      };
    return APIBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static APIFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<APIFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<APIFutureStub>() {
        @java.lang.Override
        public APIFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new APIFutureStub(channel, callOptions);
        }
      };
    return APIFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * API is the main Liftbridge server interface clients interact with.
   * </pre>
   */
  public static abstract class APIImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * CreateStream creates a new stream attached to a NATS subject. It returns
     * an AlreadyExists status code if a stream with the given subject and name
     * already exists.
     * </pre>
     */
    public void createStream(io.liftbridge.proto.Api.CreateStreamRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.CreateStreamResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateStreamMethod(), responseObserver);
    }

    /**
     * <pre>
     * Subscribe creates an ephemeral subscription for the given stream. It
     * begins to receive messages starting at the given offset and waits for
     * new messages when it reaches the end of the stream. Use the request
     * context to close the subscription.
     * </pre>
     */
    public void subscribe(io.liftbridge.proto.Api.SubscribeRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.Message> responseObserver) {
      asyncUnimplementedUnaryCall(getSubscribeMethod(), responseObserver);
    }

    /**
     * <pre>
     * FetchMetadata retrieves the latest cluster metadata, including stream
     * broker information.
     * </pre>
     */
    public void fetchMetadata(io.liftbridge.proto.Api.FetchMetadataRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.FetchMetadataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFetchMetadataMethod(), responseObserver);
    }

    /**
     * <pre>
     * Publish a new message to a subject. If the AckPolicy is not NONE and a
     * deadline is provided, this will synchronously block until the ack is
     * received. If the ack is not received in time, a DeadlineExceeded status
     * code is returned.
     * </pre>
     */
    public void publish(io.liftbridge.proto.Api.PublishRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.PublishResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPublishMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCreateStreamMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.liftbridge.proto.Api.CreateStreamRequest,
                io.liftbridge.proto.Api.CreateStreamResponse>(
                  this, METHODID_CREATE_STREAM)))
          .addMethod(
            getSubscribeMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                io.liftbridge.proto.Api.SubscribeRequest,
                io.liftbridge.proto.Api.Message>(
                  this, METHODID_SUBSCRIBE)))
          .addMethod(
            getFetchMetadataMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.liftbridge.proto.Api.FetchMetadataRequest,
                io.liftbridge.proto.Api.FetchMetadataResponse>(
                  this, METHODID_FETCH_METADATA)))
          .addMethod(
            getPublishMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                io.liftbridge.proto.Api.PublishRequest,
                io.liftbridge.proto.Api.PublishResponse>(
                  this, METHODID_PUBLISH)))
          .build();
    }
  }

  /**
   * <pre>
   * API is the main Liftbridge server interface clients interact with.
   * </pre>
   */
  public static final class APIStub extends io.grpc.stub.AbstractAsyncStub<APIStub> {
    private APIStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected APIStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new APIStub(channel, callOptions);
    }

    /**
     * <pre>
     * CreateStream creates a new stream attached to a NATS subject. It returns
     * an AlreadyExists status code if a stream with the given subject and name
     * already exists.
     * </pre>
     */
    public void createStream(io.liftbridge.proto.Api.CreateStreamRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.CreateStreamResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Subscribe creates an ephemeral subscription for the given stream. It
     * begins to receive messages starting at the given offset and waits for
     * new messages when it reaches the end of the stream. Use the request
     * context to close the subscription.
     * </pre>
     */
    public void subscribe(io.liftbridge.proto.Api.SubscribeRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.Message> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * FetchMetadata retrieves the latest cluster metadata, including stream
     * broker information.
     * </pre>
     */
    public void fetchMetadata(io.liftbridge.proto.Api.FetchMetadataRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.FetchMetadataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFetchMetadataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Publish a new message to a subject. If the AckPolicy is not NONE and a
     * deadline is provided, this will synchronously block until the ack is
     * received. If the ack is not received in time, a DeadlineExceeded status
     * code is returned.
     * </pre>
     */
    public void publish(io.liftbridge.proto.Api.PublishRequest request,
        io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.PublishResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPublishMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * API is the main Liftbridge server interface clients interact with.
   * </pre>
   */
  public static final class APIBlockingStub extends io.grpc.stub.AbstractBlockingStub<APIBlockingStub> {
    private APIBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected APIBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new APIBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * CreateStream creates a new stream attached to a NATS subject. It returns
     * an AlreadyExists status code if a stream with the given subject and name
     * already exists.
     * </pre>
     */
    public io.liftbridge.proto.Api.CreateStreamResponse createStream(io.liftbridge.proto.Api.CreateStreamRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateStreamMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Subscribe creates an ephemeral subscription for the given stream. It
     * begins to receive messages starting at the given offset and waits for
     * new messages when it reaches the end of the stream. Use the request
     * context to close the subscription.
     * </pre>
     */
    public java.util.Iterator<io.liftbridge.proto.Api.Message> subscribe(
        io.liftbridge.proto.Api.SubscribeRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * FetchMetadata retrieves the latest cluster metadata, including stream
     * broker information.
     * </pre>
     */
    public io.liftbridge.proto.Api.FetchMetadataResponse fetchMetadata(io.liftbridge.proto.Api.FetchMetadataRequest request) {
      return blockingUnaryCall(
          getChannel(), getFetchMetadataMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Publish a new message to a subject. If the AckPolicy is not NONE and a
     * deadline is provided, this will synchronously block until the ack is
     * received. If the ack is not received in time, a DeadlineExceeded status
     * code is returned.
     * </pre>
     */
    public io.liftbridge.proto.Api.PublishResponse publish(io.liftbridge.proto.Api.PublishRequest request) {
      return blockingUnaryCall(
          getChannel(), getPublishMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * API is the main Liftbridge server interface clients interact with.
   * </pre>
   */
  public static final class APIFutureStub extends io.grpc.stub.AbstractFutureStub<APIFutureStub> {
    private APIFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected APIFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new APIFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * CreateStream creates a new stream attached to a NATS subject. It returns
     * an AlreadyExists status code if a stream with the given subject and name
     * already exists.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.liftbridge.proto.Api.CreateStreamResponse> createStream(
        io.liftbridge.proto.Api.CreateStreamRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateStreamMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * FetchMetadata retrieves the latest cluster metadata, including stream
     * broker information.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.liftbridge.proto.Api.FetchMetadataResponse> fetchMetadata(
        io.liftbridge.proto.Api.FetchMetadataRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFetchMetadataMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Publish a new message to a subject. If the AckPolicy is not NONE and a
     * deadline is provided, this will synchronously block until the ack is
     * received. If the ack is not received in time, a DeadlineExceeded status
     * code is returned.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.liftbridge.proto.Api.PublishResponse> publish(
        io.liftbridge.proto.Api.PublishRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPublishMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_STREAM = 0;
  private static final int METHODID_SUBSCRIBE = 1;
  private static final int METHODID_FETCH_METADATA = 2;
  private static final int METHODID_PUBLISH = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final APIImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(APIImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_STREAM:
          serviceImpl.createStream((io.liftbridge.proto.Api.CreateStreamRequest) request,
              (io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.CreateStreamResponse>) responseObserver);
          break;
        case METHODID_SUBSCRIBE:
          serviceImpl.subscribe((io.liftbridge.proto.Api.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.Message>) responseObserver);
          break;
        case METHODID_FETCH_METADATA:
          serviceImpl.fetchMetadata((io.liftbridge.proto.Api.FetchMetadataRequest) request,
              (io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.FetchMetadataResponse>) responseObserver);
          break;
        case METHODID_PUBLISH:
          serviceImpl.publish((io.liftbridge.proto.Api.PublishRequest) request,
              (io.grpc.stub.StreamObserver<io.liftbridge.proto.Api.PublishResponse>) responseObserver);
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
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class APIBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    APIBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.liftbridge.proto.Api.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("API");
    }
  }

  private static final class APIFileDescriptorSupplier
      extends APIBaseDescriptorSupplier {
    APIFileDescriptorSupplier() {}
  }

  private static final class APIMethodDescriptorSupplier
      extends APIBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    APIMethodDescriptorSupplier(String methodName) {
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
      synchronized (APIGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new APIFileDescriptorSupplier())
              .addMethod(getCreateStreamMethod())
              .addMethod(getSubscribeMethod())
              .addMethod(getFetchMetadataMethod())
              .addMethod(getPublishMethod())
              .build();
        }
      }
    }
    return result;
  }
}
