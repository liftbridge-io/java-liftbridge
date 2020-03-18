package io.liftbridge;

import io.liftbridge.proto.Api;
import com.google.protobuf.ByteString;

import java.util.Map;

/**
 * Internal API for building {@link Api.PublishRequest} to publish messages to Liftbridge.
 */
class PublishRequest {

    static Api.PublishRequest buildForStream(String streamName, byte[] payload, MessageOptions opts) {
        Integer partition = opts.getPartition();
        if (partition == null) {
            partition = opts.getPartitioner().partition(streamName, opts.getKey(), payload, opts);
        }

        Api.PublishRequest.Builder requestBuilder = Api.PublishRequest.newBuilder()
                .setStream(streamName)
                .setPartition(partition);

        return getPublishRequest(payload, opts, requestBuilder);
    }

    static Api.PublishRequest buildForSubject(String subject, byte[] payload, MessageOptions opts) {
        Api.PublishRequest.Builder requestBuilder = Api.PublishRequest.newBuilder().setSubject(subject);
        return getPublishRequest(payload, opts, requestBuilder);
    }

    private static Api.PublishRequest getPublishRequest(byte[] payload, MessageOptions opts,
                                                        Api.PublishRequest.Builder requestBuilder) {

        requestBuilder.setAckPolicy(opts.getAckPolicy().toProto());
        if (payload != null) {
            requestBuilder.setValue(ByteString.copyFrom(payload));
        }
        if (opts.getAckInbox() != null) {
            requestBuilder.setAckInbox(opts.getAckInbox());
        }
        if (opts.getCorrelationId() != null) {
            requestBuilder.setCorrelationId(opts.getCorrelationId());
        }

        byte[] msgKey = opts.getKey();
        if (msgKey != null) {
            requestBuilder.setKey(ByteString.copyFrom(msgKey));
        }

        Map<String, byte[]> headers = opts.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, byte[]> header : headers.entrySet()) {
                requestBuilder.putHeaders(
                        header.getKey(), ByteString.copyFrom(header.getValue()));
            }
        }

        return requestBuilder.build();
    }

}
