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
    private APIStub asyncStub;
    private APIBlockingStub blockingStub;
    private Client(APIStub asyncStub, APIBlockingStub blockingStub) {
        this.asyncStub = asyncStub;
        this.blockingStub = blockingStub;
    }

    /**
     * Creates a client connected to the API using the provided gRPC channel.
     */
    public static Client connect(ManagedChannel grpcChannel) {
        return new Client(APIGrpc.newStub(grpcChannel),
                          APIGrpc.newBlockingStub(grpcChannel));
    }


    /**
     * Creates a stream. Will block until response is received.
     */
    public void createStream(String streamName, StreamOptions opts) {
        CreateStreamRequest req = opts.asRequest(streamName);
        blockingStub.createStream(req);
    }

    /**
     * Subscribes to a stream.
     */
    public void subscribe(String streamName, SubscribeOptions opts) {

    }

    public static final class StreamOptions {
        private CreateStreamRequest.Builder requestBuilder;

        public StreamOptions() {
            requestBuilder = CreateStreamRequest.newBuilder();
        }

        /**
		 * @return the subject
		 */
		public String getSubject() {
			return requestBuilder.getSubject();
		}

		public StreamOptions setSubject(String subject) {
            requestBuilder.setSubject(subject);
            return this;
        }

        /**
		 * @return the replicationFactor
		 */
		public Integer getReplicationFactor() {
			return requestBuilder.getReplicationFactor();
		}

		public StreamOptions setReplicationFactor(Integer replicationFactor) {
            requestBuilder = requestBuilder.setReplicationFactor(replicationFactor);
            return this;
        }

		/**
		 * @return the group
		 */
		public String getGroup() {
			return requestBuilder.getGroup();
		}

		/**
		 * @param group the group to set
		 */
		public StreamOptions setGroup(String group) {
			requestBuilder = requestBuilder.setGroup(group);
            return this;
		}

		/**
		 * @return the partitions
		 */
		public Integer getPartitions() {
			return requestBuilder.getPartitions();
		}

		/**
		 * @param partitions the partitions to set
		 */
		public StreamOptions setPartitions(Integer partitions) {
			requestBuilder = requestBuilder.setPartitions(partitions);
            return this;
		}

        protected CreateStreamRequest asRequest(String stream_name){
            CreateStreamRequest.Builder req_builder = requestBuilder.setName(stream_name);
            if(req_builder.getSubject() == null || req_builder.getSubject() == "") {
                return req_builder.setSubject(stream_name).build();
            }
            return req_builder.build();
        }
    }

    public static final class SubscribeOptions {
        private SubscribeRequest.Builder requestBuilder;
        private MessageHandler messageHandler;
        public SubscribeOptions() {
            requestBuilder = SubscribeRequest.newBuilder();
        }

        /**
         * Specifies the stream partition to consume. Defaults to 0.
         */
        public SubscribeOptions setPartition(int partition) {
            requestBuilder = requestBuilder.setPartition(partition);
            return this;
        }

        public int getPartition() {
            return requestBuilder.getPartition();
        }

        public SubscribeOptions setStartPosition() {
            return this;
        }

        public MessageHandler getMessageHandler() {
            return messageHandler;
        }
    }
}
