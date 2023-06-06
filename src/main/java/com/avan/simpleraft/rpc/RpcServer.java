package com.avan.simpleraft.rpc;

import java.lang.reflect.Method;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web.Client;

import com.avan.simpleraft.RaftNode;
import com.avan.simpleraft.constant.RequestType;
import com.avan.simpleraft.proto.AppendResult;
import com.avan.simpleraft.proto.ClientResponse;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.proto.Response;
import com.avan.simpleraft.proto.VoteResult;
import com.avan.simpleraft.rpc.core.RpcRequest;
import com.avan.simpleraft.rpc.handler.RpcServerHandler;
import com.avan.simpleraft.rpc.protocol.NettyServer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RpcServer extends RpcServerHandler implements RpcServerInterface{

    private String ip;
    private int port;
    private RaftNode raftNode;



    public RpcServer(int port) {
        this.ip = "localhost";
        this.port = port;
    }

    public RpcServer(int port, RaftNode raftNode) {
        this.ip = "localhost";
        this.port = port;
        this.raftNode = raftNode;
    }

    @Override
    protected Object invoke(RpcRequest request){
        try{
            Class<?> clazz = Class.forName(request.getClassName());
            Object bean = this;
            Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
            Object[] params = request.getParams();
            Object res = method.invoke(bean, params);
            return res;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Response handleReq(Request request){
        if(request.getRequestType() == RequestType.R_VOTE){
            VoteResult voteResult = raftNode.requestVote(request);
            return new Response<VoteResult>(voteResult);
        }else if(request.getRequestType() == RequestType.A_ENTRIES){
            AppendResult appendResult =  raftNode.appendEntries(request);
            return new Response<AppendResult>(appendResult);
        }else if(request.getRequestType() == RequestType.CLIENT_REQ){
            ClientResponse clientResponse = raftNode.proposeRequest(request);
            return new Response<ClientResponse>(clientResponse);
        }

        return null;
    }

    public void startRpcServer(){
        NettyServer.startRun(ip, port, this);
    }

    

}
