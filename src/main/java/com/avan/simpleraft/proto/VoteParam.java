package com.avan.simpleraft.proto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteParam implements Serializable{
    
    // 候选人的当前任期:在竞选的时候 任期大的才能成为leader 防止竞选产生死锁
    private int term;

    //选民id（ip:port）
    private String peerAddr;

    //候选人id（ip:port）
    private String candateAddr;

    //候选人最新日志任期 保证日志数据的一致性 必须拥有最新日志的才能成为leader
    private int candateLogTerm;

    //候选人最新日志index
    private long candateLogIndex;


}
