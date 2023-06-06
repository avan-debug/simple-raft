package com.avan.simpleraft.rpc.core;

import java.io.Serializable;

public class RpcResponse implements Serializable{
    private String msg;
    private Object data;

    public RpcResponse() {
    }
    public RpcResponse(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }

    

    

}
