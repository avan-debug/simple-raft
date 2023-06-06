package com.avan.simpleraft.rpc.core;

import io.netty.util.concurrent.Promise;

public class RpcFuture<T> {
    private Promise<T> promise;

    public RpcFuture(Promise<T> promise) {
        this.promise = promise;
    }

    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }

    
    
}
