package com.avan.simpleraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.avan.simpleraft.rpc.service.HelloService;
import com.avan.simpleraft.rpc.protocol.NettyServer;
import com.avan.simpleraft.rpc.service.HelloServiceImpl;
import com.avan.simpleraft.rpc.spring.SpringBeansManager;

@ComponentScan(basePackages = {"com.avan.simpleraft"})
@SpringBootApplication
public class SimpleRaftApplication {

	public static void main(String[] args) {
		// SpringApplication.run(SimpleRaftApplication.class, args);
		System.setProperty("server.port", "9000");
		SpringApplication.run(SimpleRaftApplication.class, args);
		// HelloService he = (HelloServiceImpl)SpringBeansManager.getBean("com.avan.simpleraft.rpc.spring.HelloServiceImpl");
		
		RaftNode raftNode = new RaftNode();
	}

}
