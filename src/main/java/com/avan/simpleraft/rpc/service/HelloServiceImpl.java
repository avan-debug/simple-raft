package com.avan.simpleraft.rpc.service;

import org.springframework.stereotype.Component;

@Component(value="HelloServiceImpl")
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello() {
        System.out.println("hello");
        return "Hi OK";
    }
    
}
