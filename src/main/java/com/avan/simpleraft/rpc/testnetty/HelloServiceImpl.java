package com.avan.simpleraft.rpc.testnetty;

import com.avan.simpleraft.rpc.impl.HelloService;

public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String msg) {
        System.out.println("run hello fun!!!");
        if(msg == null){
            return "no msg";
        }
        return "ok";
    }

    
    
}
