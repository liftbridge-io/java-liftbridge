package io.liftbridge;

import io.grpc.Context;

public class Subscription {

    private Context.CancellableContext context;

    private Subscription() {
    }

    static Subscription fromGrpc(Context.CancellableContext ctx) {
        Subscription sub = new Subscription();
        sub.context = ctx;
        return sub;
    }

    public void unsubscribe() {
        context.close();
    }

}
