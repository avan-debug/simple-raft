# Details

Date : 2023-06-06 23:23:49

Directory d:\\Program Files\\VSCodeProject\\simple-raft

Total : 66 files,  2338 codes, 233 comments, 691 blanks, all 3262 lines

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [.mvn/wrapper/maven-wrapper.properties](/.mvn/wrapper/maven-wrapper.properties) | Java Properties | 2 | 0 | 1 | 3 |
| [README.md](/README.md) | Markdown | 1 | 0 | 1 | 2 |
| [mvnw.cmd](/mvnw.cmd) | Batch | 118 | 51 | 37 | 206 |
| [pom.xml](/pom.xml) | XML | 83 | 6 | 18 | 107 |
| [rocksdb/9000/logModule/000033.log](/rocksdb/9000/logModule/000033.log) | Log | 0 | 0 | 1 | 1 |
| [rocksdb/9000/stateMachine/000033.log](/rocksdb/9000/stateMachine/000033.log) | Log | 0 | 0 | 1 | 1 |
| [rocksdb/null/logModule/000042.log](/rocksdb/null/logModule/000042.log) | Log | 1 | 0 | 0 | 1 |
| [rocksdb/null/logsdir/000052.log](/rocksdb/null/logsdir/000052.log) | Log | 3 | 0 | 0 | 3 |
| [rocksdb/null/stateMachine/000036.log](/rocksdb/null/stateMachine/000036.log) | Log | 0 | 0 | 1 | 1 |
| [src/log4j.properties](/src/log4j.properties) | Java Properties | 0 | 0 | 1 | 1 |
| [src/main/java/com/avan/simpleraft/RaftNode.java](/src/main/java/com/avan/simpleraft/RaftNode.java) | Java | 601 | 92 | 136 | 829 |
| [src/main/java/com/avan/simpleraft/SimpleRaftApplication.java](/src/main/java/com/avan/simpleraft/SimpleRaftApplication.java) | Java | 13 | 2 | 7 | 22 |
| [src/main/java/com/avan/simpleraft/config/RaftConfig.java](/src/main/java/com/avan/simpleraft/config/RaftConfig.java) | Java | 11 | 0 | 8 | 19 |
| [src/main/java/com/avan/simpleraft/constant/Command.java](/src/main/java/com/avan/simpleraft/constant/Command.java) | Java | 15 | 0 | 7 | 22 |
| [src/main/java/com/avan/simpleraft/constant/CommandType.java](/src/main/java/com/avan/simpleraft/constant/CommandType.java) | Java | 6 | 0 | 3 | 9 |
| [src/main/java/com/avan/simpleraft/constant/NodeInfo.java](/src/main/java/com/avan/simpleraft/constant/NodeInfo.java) | Java | 5 | 0 | 2 | 7 |
| [src/main/java/com/avan/simpleraft/constant/NodeState.java](/src/main/java/com/avan/simpleraft/constant/NodeState.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/avan/simpleraft/constant/RequestType.java](/src/main/java/com/avan/simpleraft/constant/RequestType.java) | Java | 8 | 5 | 2 | 15 |
| [src/main/java/com/avan/simpleraft/db/StateMachine.java](/src/main/java/com/avan/simpleraft/db/StateMachine.java) | Java | 123 | 12 | 26 | 161 |
| [src/main/java/com/avan/simpleraft/log4j.properties](/src/main/java/com/avan/simpleraft/log4j.properties) | Java Properties | 0 | 0 | 1 | 1 |
| [src/main/java/com/avan/simpleraft/log/LogEntry.java](/src/main/java/com/avan/simpleraft/log/LogEntry.java) | Java | 16 | 2 | 9 | 27 |
| [src/main/java/com/avan/simpleraft/log/LogModule.java](/src/main/java/com/avan/simpleraft/log/LogModule.java) | Java | 125 | 5 | 28 | 158 |
| [src/main/java/com/avan/simpleraft/proto/AppendParam.java](/src/main/java/com/avan/simpleraft/proto/AppendParam.java) | Java | 20 | 7 | 15 | 42 |
| [src/main/java/com/avan/simpleraft/proto/AppendResult.java](/src/main/java/com/avan/simpleraft/proto/AppendResult.java) | Java | 27 | 2 | 12 | 41 |
| [src/main/java/com/avan/simpleraft/proto/ClientRequest.java](/src/main/java/com/avan/simpleraft/proto/ClientRequest.java) | Java | 16 | 1 | 10 | 27 |
| [src/main/java/com/avan/simpleraft/proto/ClientResponse.java](/src/main/java/com/avan/simpleraft/proto/ClientResponse.java) | Java | 26 | 6 | 12 | 44 |
| [src/main/java/com/avan/simpleraft/proto/Request.java](/src/main/java/com/avan/simpleraft/proto/Request.java) | Java | 22 | 3 | 7 | 32 |
| [src/main/java/com/avan/simpleraft/proto/Response.java](/src/main/java/com/avan/simpleraft/proto/Response.java) | Java | 18 | 0 | 11 | 29 |
| [src/main/java/com/avan/simpleraft/proto/VoteParam.java](/src/main/java/com/avan/simpleraft/proto/VoteParam.java) | Java | 17 | 5 | 11 | 33 |
| [src/main/java/com/avan/simpleraft/proto/VoteResult.java](/src/main/java/com/avan/simpleraft/proto/VoteResult.java) | Java | 27 | 2 | 13 | 42 |
| [src/main/java/com/avan/simpleraft/rpc/RpcClient.java](/src/main/java/com/avan/simpleraft/rpc/RpcClient.java) | Java | 34 | 0 | 12 | 46 |
| [src/main/java/com/avan/simpleraft/rpc/RpcServer.java](/src/main/java/com/avan/simpleraft/rpc/RpcServer.java) | Java | 63 | 0 | 17 | 80 |
| [src/main/java/com/avan/simpleraft/rpc/RpcServerInterface.java](/src/main/java/com/avan/simpleraft/rpc/RpcServerInterface.java) | Java | 7 | 0 | 3 | 10 |
| [src/main/java/com/avan/simpleraft/rpc/codec/RpcDecoder.java](/src/main/java/com/avan/simpleraft/rpc/codec/RpcDecoder.java) | Java | 58 | 0 | 8 | 66 |
| [src/main/java/com/avan/simpleraft/rpc/codec/RpcEncoder.java](/src/main/java/com/avan/simpleraft/rpc/codec/RpcEncoder.java) | Java | 31 | 0 | 5 | 36 |
| [src/main/java/com/avan/simpleraft/rpc/constants/ReqType.java](/src/main/java/com/avan/simpleraft/rpc/constants/ReqType.java) | Java | 21 | 0 | 7 | 28 |
| [src/main/java/com/avan/simpleraft/rpc/constants/RpcConstant.java](/src/main/java/com/avan/simpleraft/rpc/constants/RpcConstant.java) | Java | 5 | 0 | 2 | 7 |
| [src/main/java/com/avan/simpleraft/rpc/constants/SerilizeType.java](/src/main/java/com/avan/simpleraft/rpc/constants/SerilizeType.java) | Java | 20 | 0 | 6 | 26 |
| [src/main/java/com/avan/simpleraft/rpc/consumer/RpcInvokerProxy.java](/src/main/java/com/avan/simpleraft/rpc/consumer/RpcInvokerProxy.java) | Java | 57 | 1 | 12 | 70 |
| [src/main/java/com/avan/simpleraft/rpc/core/Header.java](/src/main/java/com/avan/simpleraft/rpc/core/Header.java) | Java | 48 | 7 | 8 | 63 |
| [src/main/java/com/avan/simpleraft/rpc/core/RequestHolder.java](/src/main/java/com/avan/simpleraft/rpc/core/RequestHolder.java) | Java | 8 | 0 | 3 | 11 |
| [src/main/java/com/avan/simpleraft/rpc/core/RpcFuture.java](/src/main/java/com/avan/simpleraft/rpc/core/RpcFuture.java) | Java | 14 | 0 | 9 | 23 |
| [src/main/java/com/avan/simpleraft/rpc/core/RpcProtocol.java](/src/main/java/com/avan/simpleraft/rpc/core/RpcProtocol.java) | Java | 24 | 0 | 13 | 37 |
| [src/main/java/com/avan/simpleraft/rpc/core/RpcRequest.java](/src/main/java/com/avan/simpleraft/rpc/core/RpcRequest.java) | Java | 40 | 0 | 27 | 67 |
| [src/main/java/com/avan/simpleraft/rpc/core/RpcResponse.java](/src/main/java/com/avan/simpleraft/rpc/core/RpcResponse.java) | Java | 24 | 0 | 9 | 33 |
| [src/main/java/com/avan/simpleraft/rpc/handler/RpcClientHandler.java](/src/main/java/com/avan/simpleraft/rpc/handler/RpcClientHandler.java) | Java | 20 | 0 | 6 | 26 |
| [src/main/java/com/avan/simpleraft/rpc/handler/RpcServerHandler.java](/src/main/java/com/avan/simpleraft/rpc/handler/RpcServerHandler.java) | Java | 45 | 0 | 12 | 57 |
| [src/main/java/com/avan/simpleraft/rpc/impl/HelloService.java](/src/main/java/com/avan/simpleraft/rpc/impl/HelloService.java) | Java | 4 | 0 | 2 | 6 |
| [src/main/java/com/avan/simpleraft/rpc/protocol/NettyClient.java](/src/main/java/com/avan/simpleraft/rpc/protocol/NettyClient.java) | Java | 90 | 1 | 27 | 118 |
| [src/main/java/com/avan/simpleraft/rpc/protocol/NettyServer.java](/src/main/java/com/avan/simpleraft/rpc/protocol/NettyServer.java) | Java | 50 | 3 | 12 | 65 |
| [src/main/java/com/avan/simpleraft/rpc/serial/ISerializer.java](/src/main/java/com/avan/simpleraft/rpc/serial/ISerializer.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/avan/simpleraft/rpc/serial/JAVASerial.java](/src/main/java/com/avan/simpleraft/rpc/serial/JAVASerial.java) | Java | 39 | 0 | 8 | 47 |
| [src/main/java/com/avan/simpleraft/rpc/serial/JSONSerial.java](/src/main/java/com/avan/simpleraft/rpc/serial/JSONSerial.java) | Java | 17 | 0 | 7 | 24 |
| [src/main/java/com/avan/simpleraft/rpc/serial/SerializerManager.java](/src/main/java/com/avan/simpleraft/rpc/serial/SerializerManager.java) | Java | 13 | 0 | 6 | 19 |
| [src/main/java/com/avan/simpleraft/rpc/service/HelloService.java](/src/main/java/com/avan/simpleraft/rpc/service/HelloService.java) | Java | 6 | 0 | 3 | 9 |
| [src/main/java/com/avan/simpleraft/rpc/service/HelloServiceImpl.java](/src/main/java/com/avan/simpleraft/rpc/service/HelloServiceImpl.java) | Java | 10 | 0 | 5 | 15 |
| [src/main/java/com/avan/simpleraft/rpc/spring/SpringBeansManager.java](/src/main/java/com/avan/simpleraft/rpc/spring/SpringBeansManager.java) | Java | 16 | 0 | 8 | 24 |
| [src/main/java/com/avan/simpleraft/rpc/testnetty/HelloServiceImpl.java](/src/main/java/com/avan/simpleraft/rpc/testnetty/HelloServiceImpl.java) | Java | 12 | 0 | 7 | 19 |
| [src/main/java/com/avan/simpleraft/rpc/testnetty/NettyClient.java](/src/main/java/com/avan/simpleraft/rpc/testnetty/NettyClient.java) | Java | 30 | 1 | 6 | 37 |
| [src/main/java/com/avan/simpleraft/rpc/testnetty/NettyClientHandler.java](/src/main/java/com/avan/simpleraft/rpc/testnetty/NettyClientHandler.java) | Java | 23 | 0 | 5 | 28 |
| [src/main/java/com/avan/simpleraft/rpc/testnetty/NettyServer.java](/src/main/java/com/avan/simpleraft/rpc/testnetty/NettyServer.java) | Java | 37 | 3 | 10 | 50 |
| [src/main/java/com/avan/simpleraft/rpc/testnetty/NettyServerHandler.java](/src/main/java/com/avan/simpleraft/rpc/testnetty/NettyServerHandler.java) | Java | 28 | 0 | 9 | 37 |
| [src/main/java/com/avan/simpleraft/service/HandleRequest.java](/src/main/java/com/avan/simpleraft/service/HandleRequest.java) | Java | 9 | 0 | 5 | 14 |
| [src/main/java/com/avan/simpleraft/service/HandleRequestImpl.java](/src/main/java/com/avan/simpleraft/service/HandleRequestImpl.java) | Java | 26 | 0 | 9 | 35 |
| [src/main/resources/application.properties](/src/main/resources/application.properties) | Java Properties | 0 | 0 | 2 | 2 |
| [src/test/java/com/avan/simpleraft/SimpleRaftApplicationTests.java](/src/test/java/com/avan/simpleraft/SimpleRaftApplicationTests.java) | Java | 93 | 16 | 16 | 125 |

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)