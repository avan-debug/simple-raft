package com.avan.simpleraft.log;

import com.avan.simpleraft.constant.Command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogEntry {

    // 任期号
    private int term;

    // 命令索引
    private Long index;

    private Command cmd;
    
    private String requestId;

}
