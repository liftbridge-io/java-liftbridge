package io.liftbridge;
import io.liftbridge.proto.Api;
import com.google.protobuf.ByteString;
import java.util.Map;

class PublishRequest {
    static Api.PublishRequest build(String streamName, byte[] payload,
                                    MessageOptions opts) {
        int partition = opts.getPartitioner().partition(
            streamName, opts.getKey(), payload, opts);

        Api.PublishRequest.Builder requestBuilder =
            Api.PublishRequest.newBuilder()
            .setValue(ByteString.copyFrom(payload))
            .setStream(streamName)
            .setPartition(partition)
            .setAckPolicy(opts.getAckPolicy().toWire());

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
