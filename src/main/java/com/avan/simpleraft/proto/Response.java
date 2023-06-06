package com.avan.simpleraft.proto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private final static long serivalVersionUID = 1L;

    private T result;


    public static Response<String> ok(){
        return new Response<String>("ok");
    }

    public static Response<String> fail(){
        return new Response<String>("fail");
    }

}
