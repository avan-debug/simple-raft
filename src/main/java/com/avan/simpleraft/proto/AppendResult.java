package com.avan.simpleraft.proto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppendResult implements Serializable{

    // 更新被请求者的任期号
    private int term;

    // 追随者包含上次的任期号和索引号 则为true
    private boolean success;
    
    public AppendResult(boolean success){
        this.success = success;
    }

    public static AppendResult ok(){
        return new AppendResult(true);
    }

    public static AppendResult ok(int term){
        return new AppendResult(term, true);
    }

    public static AppendResult fail(){
        return new AppendResult(false);
    }

    public static AppendResult fail(int term){
        return new AppendResult(term, false);
    }

}
