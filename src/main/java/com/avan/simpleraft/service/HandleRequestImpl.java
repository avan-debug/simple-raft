package com.avan.simpleraft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.avan.simpleraft.RaftNode;
import com.avan.simpleraft.constant.RequestType;
import com.avan.simpleraft.proto.AppendResult;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.proto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component(value="HandleRequestImpl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandleRequestImpl implements HandleRequest{

    private RaftNode node;

    public Response<AppendResult> handleReq(Request request){
        if(request.getRequestType() == RequestType.R_VOTE){

        }else if(request.getRequestType() == RequestType.A_ENTRIES){
            AppendResult appendResult =  node.appendEntries(request);
            return new Response<AppendResult>(appendResult);
        }

        return null;
    }
}
