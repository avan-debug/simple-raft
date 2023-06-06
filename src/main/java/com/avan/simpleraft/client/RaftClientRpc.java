package com.avan.simpleraft.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.avan.simpleraft.rpc.RpcClient;

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

    

    

}
