package com.avan.simpleraft.db;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.alibaba.fastjson.JSON;
import com.avan.simpleraft.constant.Command;
import com.avan.simpleraft.constant.CommandType;
import com.avan.simpleraft.log.LogEntry;

import lombok.extern.slf4j.Slf4j;
/**
 * read store db
 */
@Slf4j
public class StateMachine {
    private String dbDir;
    private String stateMachineDir; 
    
    private RocksDB rocksDB;

    private static StateMachine logModule;
    private final byte[] LAST_COMMIT_KEY = "LAST_COMMIT_KEY".getBytes();


    private StateMachine() {
        if(dbDir == null){
            dbDir = "./rocksdb/" + System.getProperty("server.port");
        }
        if(stateMachineDir == null){
            stateMachineDir = dbDir + "/stateMachine";
        }

        RocksDB.loadLibrary();
        final Options options = new Options().setCreateIfMissing(true);

        File file = new File(stateMachineDir);
        boolean success = false;
        if(!file.exists()){
            file.mkdirs();
        }
        if(success)
            log.info("mkdir {} success", stateMachineDir);

        try{
            rocksDB = RocksDB.open(options, stateMachineDir);
        }catch(Exception e){
            log.warn(e.getMessage());
        }
    }

    public static StateMachine getInstence(){
        if(logModule == null){
            synchronized(StateMachine.class){
                if(logModule == null)
                    return new StateMachine();
            }
        }
        return logModule;
    }

    public void destory(){
        rocksDB.close();
        log.info("close rocksDB success!!!");
    }

    public String getString(String key){
        try {
            byte[] bytes = rocksDB.get(key.getBytes());
            if(bytes != null)
                return new String(bytes);
        } catch (Exception e) {
            log.error("stateMachine read exception : {}", e);
        }
        return null;
    }

    public void setString(String key, String val){
        try {
            rocksDB.put(key.getBytes(), val.getBytes());
        } catch (RocksDBException e) {
            log.info("set string error : {}", e.getMessage());
        }
    }

    public void delString(String ... keys){
        try {
            for (String key : keys) {
                rocksDB.delete(key.getBytes());
            }
        } catch (RocksDBException e) {
            log.info("delete string error : {}", e.getMessage());
        }
    }

    public synchronized void apply(LogEntry logEntry){
        Command command = logEntry.getCmd();
        if(command == null){
            setCommit(getLastCommit() + 1);
            return;
        }
        CommandType commandType = command.getCommandType();
        String key = command.getKey();
        String val = command.getVal();
        try {
            switch(commandType){
                case PUT:
                    setString(key, val);
                    break;
                case DELETE:
                    delString(key);
                    break;
                default:
                    break;
    
            }
            setCommit(getLastCommit() + 1);
        } catch (Exception e) {
            log.error("apply logEntry error {}", e.getMessage());
        }finally{
            // 保证幂等性 请求id
            setString(logEntry.getRequestId(), "1");
        } 
    }


    /**
     * 获取最后的提交命令
     * @return
     */
    public synchronized Long getLastCommit(){
        Long lastCommitIndex = (long)-1;
        try {
            lastCommitIndex = Long.parseLong(new String(rocksDB.get(LAST_COMMIT_KEY)));
        } catch (Exception e) {
            log.error("get last commit error : {}", e.getMessage());
        }
        return lastCommitIndex;
    }

    /**
     * 设置提交命令
     * @return
     */
    public synchronized void setCommit(Long index){
        try {
            rocksDB.put(LAST_COMMIT_KEY, index.toString().getBytes());
        } catch (Exception e) {
            log.error("update last commit error : {}", e.getMessage());
        }
    }




}
