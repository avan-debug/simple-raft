package com.avan.simpleraft.rpc.serial;

public interface ISerializer {
    public <T> byte[] serialize(T o);
    public <T> T reserialize(byte[] in, Class<T> clazz);
    byte getType();
}
