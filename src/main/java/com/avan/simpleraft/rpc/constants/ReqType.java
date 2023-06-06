package com.avan.simpleraft.rpc.constants;

public enum ReqType {
    REQUEST((byte)0),
    RESPONSE((byte)1),
    HEARTBEAT((byte)2);

    private byte code;

    private ReqType(byte code){
        this.code = code;
    }

    public byte code(){
        return code;
    }

    public static ReqType findByCode(int code){
        for(ReqType msgType : ReqType.values()){
            if(msgType.code() == code){
                return msgType;
            }
        }
        return null;
    }

}
