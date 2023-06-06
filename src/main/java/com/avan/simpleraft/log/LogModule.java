package com.avan.simpleraft.log;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogModule {
    private String dbDir; 
    private String logsDir;
    
    private RocksDB rocksDB;
    private ReentrantLock lock = new ReentrantLock(false);

    private static LogModule logModule;
    private final byte[] LAST_INDEX_KEY = "LAST_INDEX_KEY".getBytes();


    private LogModule() {
        if(dbDir == null){
            dbDir = "./rocksdb/" + System.getProperty("server.port");
        }
        if(logsDir == null){
            logsDir = dbDir + "/logModule";
        }

        RocksDB.loadLibrary();
        final Options options = new Options().setCreateIfMissing(true);

        File file = new File(logsDir);
        boolean success = false;
        if(!file.exists()){
            file.mkdirs();
        }
        if(success)
            log.info("mkdir {} success", logsDir);

        try{
            rocksDB = RocksDB.open(options, logsDir);
        }catch(Exception e){
            log.warn(e.getMessage());
        }
    }

    public static LogModule getInstence(){
        if(logModule == null){
            synchronized(LogModule.class){
                if(logModule == null)
                    return new LogModule();
            }
        }
        return logModule;
    }

    public void destory(){
        rocksDB.close();
        log.info("close rocksDB success!!!");
    }

    /**
     * 将日志追加到文件末尾
     * 失败也算一次
     * @param logEntry
     */
    public void write(LogEntry logEntry){
        boolean success = false;
        try{
            lock.lock();
            logEntry.setIndex(getLastIndex() + 1);
            success = true;
            rocksDB.put(logEntry.getIndex().toString().getBytes(), JSON.toJSONBytes(logEntry));
        }catch(RocksDBException e){
            log.error("rocksDB write exception : {}", e.getMessage());
        }finally{
            if(success)
                updateLastIndex(logEntry.getIndex() + 1);
            lock.unlock();
        }

    }

    public LogEntry read(Long index){
        LogEntry res = null;
        try {
            byte[] objBytes = rocksDB.get(index.toString().getBytes());
            res = JSON.parseObject(objBytes, LogEntry.class);  
        } catch (RocksDBException e) {
            log.error("rocksDB read exception : {}", e.getMessage());
        }
        return res;
    }

    public void deleteFromStartIndex(Long startIndex){
        boolean success = false;
        try{
            lock.lock();
            Long lastIndex = getLastIndex();
            for (Long i = startIndex; i < lastIndex; i++) {
                rocksDB.delete(i.toString().getBytes());
            }
            success = true;
            log.warn("rocksDB delete frome {} to {}", startIndex, lastIndex);
        }catch(RocksDBException e){
            log.error("rocksDB write exception : {}", e.getMessage());
        }finally{
            if(success)
                updateLastIndex(startIndex - 1);
            lock.unlock();
        }
    }

    public Long getLastIndex(){
        byte[] res = "-1".getBytes();
        try{
            res = rocksDB.get(LAST_INDEX_KEY);
            if(res == null){
                res = "-1".getBytes();
            }
        }catch(RocksDBException e){
            log.error("rocksDB get last index exception : {}", e.getMessage());
        }
        return Long.valueOf(new String(res));
    }

    public void updateLastIndex(Long index){
        lock.lock();
        try{
            rocksDB.put(LAST_INDEX_KEY, index.toString().getBytes());
        }catch(RocksDBException e){
            log.error("rocksDB get last index exception : {}", e.getMessage());
        }
        lock.unlock();
    }

    public LogEntry getLast(){
        try {
            Long lastIndex = Long.valueOf(new String(rocksDB.get(LAST_INDEX_KEY)));
            return read(lastIndex);
        } catch (Exception e) {
            log.error("get last error : ", e.getMessage());
        }

        return null;
    }



    
    
}
