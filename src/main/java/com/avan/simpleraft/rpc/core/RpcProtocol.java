package com.avan.simpleraft.rpc.core;

import java.io.Serializable;

public class RpcProtocol<T> implements Serializable{

    private Header header;
    private T content;
    
    public RpcProtocol() {
    }

    public RpcProtocol(Header header, T content) {
        this.header = header;
        this.content = content;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    
    
}
