package com.avan.simpleraft.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.avan.simpleraft.constant.CommandType;
import com.avan.simpleraft.constant.RequestType;
import com.avan.simpleraft.proto.ClientRequest;
import com.avan.simpleraft.proto.ClientResponse;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.rpc.RpcClient;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RaftClientRpc {
    
    static List<String> serverAddrs;

    private RpcClient rpcClient;

    private int index;
    private int size;
    private String addr;

    public RaftClientRpc() {
        String[] addrs = new String[]{"localhost:8775", "localhost:8776", "localhost:8777", "localhost:8778", "localhost:8779"};
        serverAddrs = new ArrayList<>();
        Collections.addAll(serverAddrs, addrs);
        index = 0;
        size = serverAddrs.size();
        addr = serverAddrs.get(0);
        String[] ipAndPort = addr.split(":");
        rpcClient = new RpcClient(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
    } 

    public String get(String key, String requestID) throws InterruptedException{
        return proposeClientRequest(key, null, requestID, CommandType.PUT);
    }

    public String put(String key, String val, String requestID) throws InterruptedException{
        return proposeClientRequest(key, val, requestID, CommandType.PUT);
    }

    public String del(String key, String requestID) throws InterruptedException{
        return proposeClientRequest(key, null, requestID, CommandType.DELETE);
    }

    public String proposeClientRequest(String key, String val, String requestID, CommandType type) throws InterruptedException{
        ClientRequest clientRequest = ClientRequest
            .builder()
            .key(key)
            .requestID(requestID)
            .type(type)
            .build();

        if(StringUtils.isNotEmpty(val))
            clientRequest.setVal(val);

        Request request = Request
            .builder()
            .param(clientRequest)
            .requestType(RequestType.CLIENT_REQ)
            .url(addr)
            .build();

        ClientResponse clientResponse = null;
        // 未获得相应 重试
        while(true){
            try {
                clientResponse = rpcClient.send(request, 500);
                
            } catch (Exception e) {
                // 请求超时
                log.error("get cmd timeout exception : {}", e);
                index = (index + 1) % size;
                addr = serverAddrs.get(index);
                request.setUrl(addr);
                Thread.sleep(300);
                continue;
            }

            if(clientResponse != null){
                int code = clientResponse.getCode();
                if(code == -1){
                    log.error("get cmd get response failed : {}", clientResponse.getMsg());
                }else if(code == 1){
                    log.info("redirect to leader : {}", clientResponse.getMsg());
                    request.setUrl((String)clientResponse.getMsg());
                }else if(code == 0){
                    if(type == CommandType.PUT || type == CommandType.DELETE)
                        return "ok";
                    return (String)clientResponse.getMsg();
                }
            }
        }
    }



    

}
