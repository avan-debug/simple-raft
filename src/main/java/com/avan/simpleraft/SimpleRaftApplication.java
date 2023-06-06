package com.avan.simpleraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


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
