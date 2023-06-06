package com.avan.simpleraft.service;

import org.springframework.stereotype.Component;
import com.avan.simpleraft.proto.AppendResult;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.proto.Response;

@Component(value="HandleRequest")
public interface HandleRequest {

    public Response<AppendResult> handleReq(Request request);

}
