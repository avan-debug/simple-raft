package com.avan.simpleraft.rpc.constants;

public enum SerilizeType {
    JAVATYPE((byte)0),
    JSONTYPE((byte)1);

    private byte code;

    private SerilizeType(byte code){
        this.code = code;
    }

    public byte code(){
        return code;
    }

    public static SerilizeType findByCode(int code){
        for(SerilizeType msgType : SerilizeType.values()){
            if(msgType.code() == code){
                return msgType;
            }
        }
        return null;
    }
}
