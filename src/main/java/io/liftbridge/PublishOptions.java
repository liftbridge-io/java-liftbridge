package io.liftbridge;

import io.liftbridge.proto.Api;

public class PublishOptions {
    private Api.Message.Builder messageBuilder;
    private AckPolicy ackPolicy = AckPolicy.NONE;

    public PublishOptions() {
        messageBuilder = Api.Message.newBuilder();
    }

    public AckPolicy getAckPolicy() {
        return this.ackPolicy;
    }

    public String getSubject() {
        return this.messageBuilder.getSubject();
    }

    public String getAckInbox() {
        return this.messageBuilder.getAckInbox();
    }

    public PublishOptions setAckPolicy(AckPolicy policy) {
        switch(policy){
        case LEADER:
            messageBuilder = messageBuilder.setAckPolicy(Api.AckPolicy.LEADER);
        case ALL:
            messageBuilder = messageBuilder.setAckPolicy(Api.AckPolicy.ALL);
        case NONE:
            messageBuilder = messageBuilder.setAckPolicy(Api.AckPolicy.NONE);
        }
        this.ackPolicy = policy;
        return this;
    }

    public PublishOptions setSubject(String subject) {
        messageBuilder = messageBuilder.setSubject(subject);
        return this;
    }

    public PublishOptions setAckInbox(String ackInbox) {
        messageBuilder = messageBuilder.setAckInbox(ackInbox);
        return this;
    }

    public enum AckPolicy {LEADER, ALL, NONE}
}
