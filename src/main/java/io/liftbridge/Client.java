package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.liftbridge.MessageHandler;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.Api.CreateStreamRequest;
import io.liftbridge.proto.Api.SubscribeRequest;

public class Client {
    private APIStub async_stub;
    private APIBlockingStub blocking_stub;
    private Client(APIStub async_stub, APIBlockingStub blocking_stub) {
        this.async_stub = async_stub;
        this.blocking_stub = blocking_stub;
    }

    /**
     * Creates a client connected to the API using the provided gRPC channel.
     */
    public static Client connect(ManagedChannel grpc_channel) {
        return new Client(APIGrpc.newStub(grpc_channel),
                          APIGrpc.newBlockingStub(grpc_channel));
    }


    /**
     * Creates a stream. Will block until response is received.
     */
    public void createStream(String stream_name, CreateStreamOptions opts) {
        CreateStreamRequest req = opts.asRequest(stream_name);
        blocking_stub.createStream(req);
    }

    /**
     * Subscribes to a stream.
     */
    public void subscribe(String stream_name, SubscribeOptions opts) {

    }

    public static final class CreateStreamOptions {
        private CreateStreamRequest.Builder create_stream_req_builder;

        public CreateStreamOptions() {
            create_stream_req_builder = CreateStreamRequest.newBuilder();
        }

        /**
		 * @return the subject
		 */
		public String getSubject() {
			return create_stream_req_builder.getSubject();
		}

		public CreateStreamOptions setSubject(String subject) {
            create_stream_req_builder.setSubject(subject);
            return this;
        }

        /**
		 * @return the replicationFactor
		 */
		public Integer getReplicationFactor() {
			return create_stream_req_builder.getReplicationFactor();
		}

		public CreateStreamOptions setReplicationFactor(Integer replicationFactor) {
            create_stream_req_builder.setReplicationFactor(replicationFactor);
            return this;
        }

		/**
		 * @return the group
		 */
		public String getGroup() {
			return create_stream_req_builder.getGroup();
		}

		/**
		 * @param group the group to set
		 */
		public CreateStreamOptions setGroup(String group) {
			create_stream_req_builder.setGroup(group);
            return this;
		}

		/**
		 * @return the partitions
		 */
		public Integer getPartitions() {
			return create_stream_req_builder.getPartitions();
		}

		/**
		 * @param partitions the partitions to set
		 */
		public CreateStreamOptions setPartitions(Integer partitions) {
			create_stream_req_builder.setPartitions(partitions);
            return this;
		}

        protected CreateStreamRequest asRequest(String stream_name){
            CreateStreamRequest.Builder req_builder = create_stream_req_builder.setName(stream_name);
            if(req_builder.getSubject() == null || req_builder.getSubject() == "") {
                return req_builder.setSubject(stream_name).build();
            }
            return req_builder.build();
        }
    }

    public static final class SubscribeOptions {
        private SubscribeRequest.Builder subscriber_req_builder;
        private MessageHandler message_handler;
        public SubscribeOptions() {
            subscriber_req_builder = SubscribeRequest.newBuilder();
        }

        /**
         * Specifies the stream partition to consume. Defaults to 0.
         */
        public void setPartition(int partition) {
            subscriber_req_builder.setPartition(partition);
        }

        public int getPartition() {
            return subscriber_req_builder.getPartition();
        }

        public void setStartPosition() {}
    }
}
