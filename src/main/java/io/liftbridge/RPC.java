package io.liftbridge;

import io.grpc.StatusRuntimeException;

interface RPC<T> {

    T execute(ManagedAPIClient client) throws StatusRuntimeException;

}
