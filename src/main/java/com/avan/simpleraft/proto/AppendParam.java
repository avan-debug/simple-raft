package com.avan.simpleraft.proto;

import java.io.Serializable;

import com.avan.simpleraft.log.LogEntry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppendParam implements Serializable{

    // 任期号
    private int term;

    // 被请求者ID
    private String serverID;

    // 领导者ID
    private String leaderID;

    // 领导给FOLLOWER发送所有日志的上一个日志任期号
    private long preLogTerm;

    // 领导给FOLLOWER发送所有日志的上一个日志任期号
    private long preLogIndex;

    // 日志集合 合并是为了充分利用资源（当是心跳信息时为空）
    private LogEntry[] logEntries;

    // 领导最新提交到db的索引值
    private long leaderLastCommit;

    
    
}
