package com.avan.simpleraft;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.avan.simpleraft.constant.Command;
import com.avan.simpleraft.constant.CommandType;
import com.avan.simpleraft.constant.RequestType;
import com.avan.simpleraft.log.LogEntry;
import com.avan.simpleraft.log.LogModule;
import com.avan.simpleraft.proto.AppendParam;
import com.avan.simpleraft.proto.AppendResult;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.rpc.RpcClient;
import com.avan.simpleraft.rpc.RpcServer;
import com.avan.simpleraft.rpc.consumer.RpcInvokerProxy;
import com.avan.simpleraft.rpc.service.HelloService;
import com.avan.simpleraft.rpc.service.HelloServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.Options;

@SpringBootTest
@Slf4j
class SimpleRaftApplicationTests {

	@Test
	void contextLoads() {
		HelloService helloService = new HelloServiceImpl();
		InvocationHandler handler = new RpcInvokerProxy("localhost", 9000);
		HelloService proxy = (HelloService)Proxy.newProxyInstance(helloService.getClass().getClassLoader(), new Class<?>[]{HelloService.class}, handler);
		String res = proxy.hello();
		System.out.println(res);
	}


	@Test
	void testRocksDB(){
  // a static method that loads the RocksDB C++ library.
		boolean openResult = true;
		RocksDB.loadLibrary();
		// RocksDB db;
		// the Options class contains a set of configurable DB options
		// that determines the behaviour of the database.
		try (final Options options = new Options().setCreateIfMissing(true)) {
			// a factory method that returns a RocksDB instance
			try (RocksDB db = RocksDB.open(options, "/home/xys/rocksdb/path/to/db")) {
				System.out.println("open success!!!");
				// do something
				byte[] key1 = new byte[]{1};
				byte[] value = new byte[]{3, 3};
				try {
					// db.put(key1, value);
					byte[] value1 = db.get(key1);
					assertArrayEquals(value, value1, "not equal!!!");
					// db.delete(key1);
				} catch (RocksDBException e) {
				// error handling
				}
				openResult = true;
			}
		} catch (RocksDBException e) {
			// do some error handling
			Assert.isTrue(openResult, "open false");
			e.printStackTrace();
		}
		
		Assert.isTrue(openResult, "open false");
	}

	@Test
	public void testLogModule(){
		LogModule logModule = LogModule.getInstence();
		long index = logModule.getLastIndex();
		// assertEquals(index, 5);
		Command command = new Command(CommandType.PUT, "fuck", "you");
		LogEntry logEntry = new LogEntry(0, null, command, "2010");
		logModule.write(logEntry);
		index = logModule.getLastIndex();
		// assertEquals(index, 6);
		LogEntry logEntry2 = logModule.read((long)0);
		System.out.println(logEntry2.getTerm());
	}

	@Test
	public void testDeleteLogModule(){
		LogModule logModule = LogModule.getInstence();
		long lastIndex = logModule.getLastIndex();
		LogEntry logEntry2 = logModule.read((long)0);
		logModule.deleteFromStartIndex((long)0);
		LogEntry logEntry3 = logModule.read((long)0);
		log.info("last index ========== {}", lastIndex);
	}

	@Test
	public void testBug(){
		int cup = Runtime.getRuntime().availableProcessors();
		System.out.println(cup);
	}

	@Test
	public void testRpcClient(){
		String ip = "localhost";
		String port = "9000";
		System.setProperty("server.port", "9000");
		// RpcServer rpcServer = new RpcServer(ip, Integer.parseInt(port));
		// rpcServer.startRpcServer();
		RpcClient rpcClient = new RpcClient(ip, Integer.parseInt(port));
		AppendParam appendParam = new AppendParam(0, ip + ":" + port, ip + ":" + port, 0, 0, null, 0);
		Request request = new Request(RequestType.A_ENTRIES, -1, appendParam, ip + ":" + port);
		AppendResult appendResult = rpcClient.send(request);
		System.out.println();
	}
}
