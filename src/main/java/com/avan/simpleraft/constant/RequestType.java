package com.avan.simpleraft.constant;

public enum RequestType {
    // 投票
    R_VOTE,
    // 追加日志
    A_ENTRIES,
    // 客户端请求
    CLIENT_REQ,
    // 配置添加（增加节点） fixed
    CHANGE_CONFIG_ADD,
    // 配置删除（删除节点） fixed
    CHANGE_CONFIG_REMOVE,
}
