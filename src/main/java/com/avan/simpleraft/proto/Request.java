package com.avan.simpleraft.proto;

import java.io.Serializable;

import com.avan.simpleraft.constant.RequestType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request implements Serializable{
    // 请求类型
    RequestType requestType;
    // 附加日志参数
    private Object param;
    // 目的地的url ip:port
    private String url;
    
    public String getIp(){
        return url.split(":")[0];
    }

    public Integer getPort(){
        return Integer.parseInt(url.split(":")[1]);
    }
}
