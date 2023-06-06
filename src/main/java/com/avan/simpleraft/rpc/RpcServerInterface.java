package com.avan.simpleraft.rpc;

import com.avan.simpleraft.proto.AppendResult;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.proto.Response;

public interface RpcServerInterface {
    public Response<AppendResult> handleReq(Request request);
}
