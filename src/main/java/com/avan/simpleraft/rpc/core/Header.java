package com.avan.simpleraft.rpc.core;

import java.io.Serializable;

public class Header implements Serializable{
    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */
    private short magic; //魔数-用来验证报文的身份（2个字节）
    private byte serialType; //序列化类型（1个字节）
    private byte reqType; //操作类型（1个字节）
    private long requestId; //请求id（8个字节）
    private int length; //数据长度（4个字节）
    public Header(){

    }
    public Header(short magic, byte serialType, byte reqType, long requestId, int length) {
        this.magic = magic;
        this.serialType = serialType;
        this.reqType = reqType;
        this.requestId = requestId;
        this.length = length;
    }
    public short getMagic() {
        return magic;
    }
    public void setMagic(short magic) {
        this.magic = magic;
    }
    public byte getSerialType() {
        return serialType;
    }
    public void setSerialType(byte serialType) {
        this.serialType = serialType;
    }
    public byte getReqType() {
        return reqType;
    }
    public void setReqType(byte reqType) {
        this.reqType = reqType;
    }
    public long getRequestId() {
        return requestId;
    }
    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }

    

    
}
