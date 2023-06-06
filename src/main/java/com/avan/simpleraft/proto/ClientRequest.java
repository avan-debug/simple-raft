package com.avan.simpleraft.proto;


import com.avan.simpleraft.constant.CommandType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequest {

    // 命令类型
    CommandType type;

    String key;

    String val;

    String requestID;

}
