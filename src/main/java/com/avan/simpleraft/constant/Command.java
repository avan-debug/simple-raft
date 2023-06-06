package com.avan.simpleraft.constant;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Command implements Serializable{
    
    private CommandType commandType;

    private String key;

    private String val;
}
