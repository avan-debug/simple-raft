package com.avan.simpleraft.rpc.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.avan.simpleraft.rpc.constants.SerilizeType;

public class JAVASerial implements ISerializer{

    @Override
    public <T> byte[] serialize(T o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            return bos.toByteArray();
        }catch(IOException e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public <T> T reserialize(byte[] in, Class<T> clazz) {
        ByteArrayInputStream bis = new ByteArrayInputStream(in);
        try{
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            return (T)obj;
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte getType() {
        return SerilizeType.JAVATYPE.code();
    }
    
}
