package com.avan.simpleraft.rpc.serial;

import com.alibaba.fastjson.JSON;
import com.avan.simpleraft.rpc.constants.SerilizeType;

public class JSONSerial implements ISerializer{

    @Override
    public <T> byte[] serialize(T o) {
        return JSON.toJSONString(o).getBytes();
    }

    @Override
    public <T> T reserialize(byte[] in, Class<T> clazz) {
        return JSON.parseObject(new String(in), clazz);
    }

    @Override
    public byte getType() {
        return SerilizeType.JSONTYPE.code();
    }
    
}
