package com.avan.simpleraft.config;

import java.util.ArrayList;
import java.util.List;

public class RaftConfig {

    public static final int heartBeatInterval = 300;

    public static final int electionTimeout = 3 * 1000;

    private static final List<String> addrs = new ArrayList<>();

    public static List<String> getAddrs(){
        return addrs;
    }

}
