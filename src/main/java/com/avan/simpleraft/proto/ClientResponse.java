package com.avan.simpleraft.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    
    /**
     * success 0
     * failed -1
     * redirect 1
     */
    int code;

    // 返回响应的消息
    private Object msg;

    public static ClientResponse ok(String msg){
        return new ClientResponse(0, msg);
    }

    public static ClientResponse ok(){
        return new ClientResponse(0, "");
    }

    public static ClientResponse fail(String msg){
        return new ClientResponse(-1, msg);
    }

    public static ClientResponse fail(){
        return new ClientResponse(-1, "");
    }

    public static ClientResponse redirect(String msg){
        return new ClientResponse(1, msg);
    }


}
