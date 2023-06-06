package com.avan.simpleraft.rpc.serial;

import java.util.concurrent.ConcurrentHashMap;

import com.avan.simpleraft.rpc.constants.SerilizeType;

public class SerializerManager {
    private static final ConcurrentHashMap<Byte, ISerializer> map = new ConcurrentHashMap<Byte, ISerializer>();

    static{
        map.put(SerilizeType.JAVATYPE.code(), new JAVASerial());
        map.put(SerilizeType.JSONTYPE.code(), new JSONSerial());
    }

    public static ISerializer getISerializer(byte code){
        return map.get(code);
    }
}
