package com.avan.simpleraft.proto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResult implements Serializable{

    // 更新候选人的任期
    private int item;

    // 是否获得选票
    private boolean voteGranted;

    public VoteResult(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }

    public static VoteResult fail(){
        return new VoteResult(false);
    }

    public static VoteResult fail(int term){
        return new VoteResult(term, false);
    }

    public static VoteResult ok(){
        return new VoteResult(true);
    }

    public static VoteResult ok(int term){
        return new VoteResult(term, true);
    }


}
