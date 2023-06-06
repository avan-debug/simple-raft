package com.avan.simpleraft.rpc.service;

import org.springframework.stereotype.Component;

@Component(value="HelloService")
public interface HelloService {
    public String hello();
}
