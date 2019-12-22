package io.liftbridge;

public class Message {
    public Message(){}
    protected static Message fromWire(io.liftbridge.proto.Api.Message msg) {
        return new Message();
    }
}
