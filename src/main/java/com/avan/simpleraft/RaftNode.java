package com.avan.simpleraft;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.avan.simpleraft.config.RaftConfig;
import com.avan.simpleraft.constant.Command;
import com.avan.simpleraft.constant.CommandType;
import com.avan.simpleraft.constant.NodeState;
import com.avan.simpleraft.constant.RequestType;
import com.avan.simpleraft.db.StateMachine;
import com.avan.simpleraft.log.LogEntry;
import com.avan.simpleraft.log.LogModule;
import com.avan.simpleraft.proto.AppendParam;
import com.avan.simpleraft.proto.AppendResult;
import com.avan.simpleraft.proto.ClientRequest;
import com.avan.simpleraft.proto.ClientResponse;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.proto.Response;
import com.avan.simpleraft.proto.VoteParam;
import com.avan.simpleraft.proto.VoteResult;
import com.avan.simpleraft.rpc.RpcClient;
import com.avan.simpleraft.rpc.RpcServer;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RaftNode{
    // 心跳时间间隔
    private int heartBeatInterval;
    // 选举超时时间
    private int electionTimeout;
    // 节点状态
    private volatile NodeState nodeState;
    // 当前任期号
    private int term;

    // 为谁投票 保证当前只能投一票
    private String voteFor;

    // 领导者地址
    private String leaderID;

    // 上一次心跳时间
    private long preHeartBeatTime;
    // 上一次选举时间
    private long preElectionTime; 

    // 集群其他节点地址 格式（ip:port）
    private List<String> peerAddrs;

    // 发送给每一个服务器的下一个日志条目的索引值？
    private Map<String, Long> nextIndexes;

    // 地址
    private String myAddr;

    // 日志模块
    private LogModule logModule;

    //状态机模块
    private StateMachine stateMachine;

    // 线程池
    private ScheduledThreadPoolExecutor se;
    private ThreadPoolExecutor te;

    // 实际执行任务
    private HeartBeatTask heartBeatTask;
    private ElectionTask electionTask;
    private ScheduledFuture<?> heartBeatTaskFuture;
    
    //Rpc 组件
    RpcServer rpcServer;
    RpcClient rpcClient;

    // 一致性信号
    private final Long consistencySignal = 1L;
    
    // 等待一致性信号唤醒的线程
    private Thread waitThread;

    // 选举时需要的锁
    private final ReentrantLock voteLock = new ReentrantLock();

    // 追加日志时需要的锁
    private final ReentrantLock appendLock = new ReentrantLock();

    // 领导者初始化信号 通知客户端当前集群状态
    private boolean leaderInitializing;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public RaftNode(){
        logModule = LogModule.getInstence();
        stateMachine = StateMachine.getInstence();
        setConfig();
        threadPoolInit();
    }

    public RaftNode(int i){
        logModule = LogModule.getInstence();
        stateMachine = StateMachine.getInstence();
        setConfig();
    }


    public void setConfig(){

        heartBeatInterval = RaftConfig.heartBeatInterval;

        electionTimeout = RaftConfig.electionTimeout;

        updatePreElectionTime();

        preHeartBeatTime = System.currentTimeMillis();

        nodeState = NodeState.FOLLOWER;

        // String port = System.getProperty("server.port");
        String port = "9000";
        
        myAddr = "localhost:" + port;

        rpcServer = new RpcServer(Integer.parseInt(port), this);

        rpcClient = new RpcClient(myAddr, Integer.parseInt(port));

        peerAddrs = RaftConfig.getAddrs();

        peerAddrs.remove(myAddr);

        LogEntry lastEntry = logModule.getLast();
        if(lastEntry != null)
            term = lastEntry.getTerm();

        waitThread = null;

        rpcServer.startRpcServer();

    }

    private void threadPoolInit() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() / 2;
        int maxPoolSize = corePoolSize * 2;

        final int queueSize = 1024;
        final long keepTime =  60 * 1000;

        se = new ScheduledThreadPoolExecutor(corePoolSize);
        te = new ThreadPoolExecutor(corePoolSize, 
                                    maxPoolSize, 
                                    keepTime,   
                                    TimeUnit.MILLISECONDS, 
                                    new LinkedBlockingDeque<Runnable>(queueSize));
 
        heartBeatTask = new HeartBeatTask();
        electionTask = new ElectionTask();
        
    }
    
    public void startRun(){
        se.scheduleAtFixedRate(electionTask, 3000, 100, TimeUnit.MILLISECONDS);
    }
    
    // 主要目的时提交空日志 即no-op日志 防止出现已提交日志被覆盖
    /**
     * 1、初始化所有节点nextIndex的值为本节点的值logLastIndex +1
     * 2、发送no-op日志、提交就领导者未提交的日志
     * 3、apply之前的数据
     * @return
     */
    public boolean leaderInit() {
        leaderInitializing = true;
        nextIndexes = new ConcurrentHashMap<>();
        for (String peer : peerAddrs) {
            nextIndexes.put(peer, getLastLogIndex() + 1);
        }

        // 生成空log module
        LogEntry emptyLogEntry = LogEntry
            .builder()
            .term(term)
            .cmd(null)
            .build();
        
        logModule.write(emptyLogEntry);
        log.info("write no-op log success, log index = {}", getLastLogIndex());

        List<Future<Boolean>> futureList = new ArrayList<>();
        Semaphore semaphore = new Semaphore(0);
        for (String peer : peerAddrs) {
            futureList.add(replication(peer, emptyLogEntry, semaphore));
        }

        try {
            semaphore.tryAcquire((int)Math.floor((peerAddrs.size() + 1) / 2), 6000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("semaphore tryAcquire failed", e);
        }

        int successNum = getReplicationResult(futureList);
        // 成功获得半数以上响应 成功当选为leader 提交日志
        if(successNum * 2 >= peerAddrs.size()){
            long nextCommitIndex = getLastCommit() + 1;
            for (long i = nextCommitIndex; i <= getLastLogIndex(); i++) {
                LogEntry tmp = logModule.read(i);
                if(tmp != null)
                    stateMachine.apply(tmp);
            }
            leaderInitializing = false;
            return true;
        }
        // 失败 删除空日志 重新发起选举
        else{
            logModule.deleteFromStartIndex(emptyLogEntry.getIndex());
            log.warn("int leaderInit function : commit no-op log failed !!!");
            nodeState = NodeState.FOLLOWER;
            voteFor = "";
            leaderID = "";
            updatePreElectionTime();
            stopHeartBeatTask();
            leaderInitializing = false;
            return false;
        }
    }

    // 处理客户端请求
    public ClientResponse proposeRequest(Request request) {
        // 如果是追随者 那么重定向
        if(nodeState == NodeState.FOLLOWER){
            log.info("i am follower, redict to leader : {}", leaderID);
            redirect(request);
        }else if(nodeState == NodeState.CANDIDATE){
            log.info("candidater propose client request fail, peer : {}", myAddr);
            return ClientResponse.fail();
        }

        if(leaderInitializing){
            log.info("leader initilizing, can not propose client request, peer : {}", myAddr);
            return ClientResponse.fail();
        }

        ClientRequest clientRequest = (ClientRequest)request.getParam();
        // 读操作
        if(clientRequest.getType() == CommandType.GET){
            // fixed 感觉可以用读写锁
            synchronized(consistencySignal){
                try {
                    // 等待一个心跳周期 保证领导者有效
                    waitThread = Thread.currentThread();
                    consistencySignal.wait();
                } catch (InterruptedException e) {
                    log.error("Thread interrepted leader is not available");
                    waitThread = null;
                    return ClientResponse.fail();
                }

                waitThread = null;
                String val = stateMachine.getString(clientRequest.getKey());
                return ClientResponse.ok(val) ;
            }
        }

        // 写命令
        Command command = Command
            .builder()
            .commandType(clientRequest.getType())
            .key(clientRequest.getKey())
            .val(clientRequest.getVal())
            .build();

        LogEntry logEntry = LogEntry
            .builder()
            .cmd(command)
            .requestId(clientRequest.getRequestID())
            .build();


        

		return null;
	}

    private ClientResponse redirect(Request request) {
        if(nodeState == NodeState.FOLLOWER && StringUtils.isNotEmpty(leaderID)){
            return ClientResponse.redirect(leaderID);
        }
        return ClientResponse.fail();
    }

    // 复制日志到FOLLOWER节点
    private Future<Boolean> replication(String peer, LogEntry lastLogEntry, Semaphore semaphore) {

        return te.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                long start = System.currentTimeMillis();
                //设置追加日志参数
                AppendParam appendParam = AppendParam
                    .builder()
                    .leaderID(myAddr)
                    .leaderLastCommit(getLastCommit())
                    .serverID(peer)
                    .build();

                // 生成日志数组
                long nextIndex = nextIndexes.get(peer);
                List<LogEntry> logEntryList = new ArrayList<>();
                for (long i = nextIndex; i < lastLogEntry.getIndex(); i++) {
                    LogEntry logEntry = logModule.read(i);
                    // fixed 为什么会是null
                    if(logEntry != null)
                    logEntryList.add(logEntry);
                }
                logEntryList.add(lastLogEntry);
                
                // 设置匹配参数 保证FOLLOWER的日志和LEADER相同
                LogEntry preLogEntry = getPreLogEntry(logEntryList.get(0));
                appendParam.setPreLogIndex(preLogEntry.getIndex());
                appendParam.setPreLogTerm(preLogEntry.getTerm());
                
                // 封装rpc请求
                Request request = Request
                    .builder()
                    .requestType(RequestType.A_ENTRIES)
                    .param(appendParam)
                    .url(peer)
                    .build();

                // 重试时间为5s
                while(System.currentTimeMillis() - start < 5 * 1000L){
                    try{
                        appendParam.setLogEntries(logEntryList.toArray(new LogEntry[0]));
                        AppendResult appendResult = rpcClient.send(request);
                        
                        // 返回null 超时或数据出错
                        if(appendResult == null){
                            log.info("in replication function follower return null result peer : {}", peer);
                            semaphore.release();
                            return false;
                        }

                        // 失败情况有两种：1、对方任期更大。2、prelog不匹配 重试
                        if(appendResult.isSuccess()){
                            log.info("in replication function follower return ok result peer : {}", peer);
                            // 更新索引信息
                            nextIndexes.put(peer, lastLogEntry.getIndex() + 1);
                            semaphore.release();
                        }else{
                            // 对方任期更新
                            if(appendResult.getTerm() > term){
                                log.info("in replication function : term is older, " + 
                                    "become follower, peer's term : {}, my term : {}", appendResult.getTerm(), term);
                                nodeState = NodeState.FOLLOWER;
                                term = appendResult.getTerm();
                                stopHeartBeatTask();
                                semaphore.release();
                                return false;
                            }
                            // prelog 不匹配问题
                            else{
                                nextIndexes.put(peer, Math.max(nextIndex - 1, 0));
                                LogEntry preLogEntry2 = logModule.read(nextIndex - 1);
                                // next index = 0， 全量复制，按理说不会出现这种情况
                                if(preLogEntry2 == null){
                                    log.info("replica from begining failed");
                                    semaphore.release();
                                    return false;
                                }else{
                                    logEntryList.add(0, preLogEntry2);
                                }
                                LogEntry preLogEntry3 = getPreLogEntry(preLogEntry2);
                                appendParam.setPreLogIndex(preLogEntry3.getIndex());
                                appendParam.setPreLogTerm(preLogEntry3.getTerm());
                            }
                        }
                    }catch(Exception e){
                        log.error("retry function exception", e);
                        semaphore.release();
                        return false;
                    }

                }

                log.info("replication retry time out peer : {}", peer);
                semaphore.release();
                return false;
            }        
        });
    }

    protected LogEntry getPreLogEntry(LogEntry logEntry) {
        LogEntry preLogEntry = logModule.read(logEntry.getIndex());
        if(preLogEntry == null){
            log.info("pre log entry is null, parameter entry is {}", logEntry);
            return LogEntry.builder().index(-1L).term(-1).cmd(null).build();
        }
        return preLogEntry;
    }

    // 心跳机制 1、发送心跳保证存活 2、发送appendEntries命令
    public class HeartBeatTask implements Runnable{
        @Override
        public void run() {
            if(nodeState != NodeState.LEADER)
                return;
            
            long currentTime = System.currentTimeMillis();

            if(preHeartBeatTime + heartBeatInterval < currentTime)
                return;

            preHeartBeatTime = currentTime;

            AppendParam appendParam = AppendParam.
                    builder()
                    .term(term)
                    .leaderID(myAddr)
                    .preLogIndex(getLastLogIndex())
                    .logEntries(null)
                    .leaderLastCommit(getLastCommit())
                    .build(); 

            List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
            Semaphore semaphore = new Semaphore(0);
            // 并行发起rpc复制并获取响应
            for (String peer : peerAddrs) {
                Request request = Request
                    .builder()
                    .param(appendParam)
                    .url(peer)
                    .requestType(RequestType.A_ENTRIES)
                    .build();

                futureList.add(te.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        try{
                            AppendResult result = rpcClient.send(request);
                            // 对方任期大 转换为跟随者
                            if(result.getTerm() > term){
                                log.info("follow new leader {}", peer);
                                nodeState = NodeState.FOLLOWER;
                                voteFor = "";
                                term = result.getTerm();
                            }
                            semaphore.release();
                            return result.isSuccess();
                        }catch(Exception e){
                            log.error("heart beat rpc fail. url = {}", peer);
                            semaphore.release();
                            return false;
                        }
                    }  
                }));

                try {
                    semaphore.tryAcquire((int)Math.floor(peerAddrs.size() / 2), 6000, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("get semaphore fail, msg : ", e.getMessage());
                }

                int successNum = getReplicationResult(futureList);
                if(waitThread != null){
                    if(successNum * 2 >= peerAddrs.size()){
                        synchronized(consistencySignal){
                            consistencySignal.notifyAll();
                        }
                    }else{
                        waitThread.interrupt();
                    }
                }

            } 
        } 
    }

    /**
     * 处理来自其他节点的心跳或append请求（非选举）, 开始时要更新选举时间
     * @param request
     * @return
     */
    public AppendResult appendEntries(Request request){
        updatePreElectionTime();
        AppendParam appendParam = (AppendParam)request.getParam();
        if(term > appendParam.getTerm()){
            log.info("reject append request from {}", request.getUrl());
            return AppendResult.fail(term);
        }
        LogEntry[] logEntries = appendParam.getLogEntries();
        try{

            appendLock.lock();

            // 如果我不是FOLLOWER(可能为leader或者candidater)收到了 心跳等请求 说明已经有比我更新的领导者
            if(nodeState != NodeState.FOLLOWER){
                log.info("become follower from {}", request.getUrl());
                term = appendParam.getTerm();
                nodeState = NodeState.FOLLOWER;
                stopHeartBeatTask();
            }

            // 更新任期
            term = appendParam.getTerm();

            // 心跳请求
            if(logEntries == null || logEntries.length == 0){
                long leaderLastCommit = appendParam.getLeaderLastCommit();
                long nextCommit = logModule.getLastIndex() + 1;
                while(nextCommit <= leaderLastCommit && logModule.read(nextCommit) == null){
                    stateMachine.apply(logModule.read(nextCommit));
                    nextCommit++;
                }
                logModule.updateLastIndex(nextCommit - 1);
                return AppendResult.ok(term);
            }

            // pre log 的匹配
            if(getLastLogIndex() < appendParam.getPreLogIndex()){
                // FOLLOWER 日志项更少 匹配无效 返回
                return AppendResult.fail(term);
            }else if(appendParam.getPreLogIndex() > 0){
                // 比较term是否一致 如： 
                // leader:1、[2,3] [2,4] [3,5]
                // follower:[2,3] [2,4] [2,5]
                long lastLogTerm = logModule.read(appendParam.getPreLogIndex()).getTerm();
                // 任期不匹配 需要重新修改日志条目
                if(lastLogTerm != appendParam.getPreLogTerm()){
                    return AppendResult.fail();
                }
            }

            // 成功匹配进行日志追加:先删除不匹配的日志请求
            // 追加日志请求
            long curLogIndex = appendParam.getPreLogIndex() + 1;
            if(curLogIndex < getLastLogIndex()){
                logModule.deleteFromStartIndex(curLogIndex);
            }

            // 开始追加日志到本地文件
            LogEntry[] leaderLogEntries = appendParam.getLogEntries();
            for (LogEntry eLogEntry : leaderLogEntries) { 
                logModule.write(eLogEntry);
            }

            // 应用日志更新到状态机
            long lastCommitIndex = getLastCommit();
            long lastLogIndex = getLastLogIndex() + 1;
            for (long i = lastCommitIndex + 1; i < lastLogIndex; i++) {
                stateMachine.apply(logModule.read(i));
            }

            return AppendResult.ok(term);
            
        }catch(Exception exception){
            log.error("append entries error {}", exception);
            return AppendResult.fail(term);
        }finally{
            updatePreElectionTime();
            appendLock.unlock();
        } 
    }




    private int getReplicationResult(List<Future<Boolean>> futureList) {
        int successNum = 0;
        for (Future<Boolean> future : futureList) {
            if(future.isDone()){
                try {
                    if(future.get())
                        successNum++;
                } catch (Exception e) {
                    log.error("future get error : {}", e.getMessage());
                }
            }
        }
        return successNum;
    }
    

    // 追随者如果在选举时间内没有收到leader的心跳 那么就开始选举任务
    // 因而在接收到心跳时要更新时间
    public class ElectionTask implements Runnable{

        @Override
        public void run() {
            // learder节点 不需要选举
            if(nodeState == NodeState.LEADER)
                return;
            
            // 未到选举时间
            long currentTime = System.currentTimeMillis();
            if(currentTime - preElectionTime < RaftConfig.electionTimeout){
                return;
            }

            nodeState = NodeState.CANDIDATE;
            voteFor = myAddr;
            leaderID = "";

            log.info("{} start election, and vote for myself", myAddr);

            List<Future<VoteResult>> futureList = new ArrayList<>();
            Semaphore semaphore = new Semaphore(0);
            
            for (String peer : peerAddrs) {
                futureList.add(te.submit(new Callable<VoteResult>() {

                    @Override
                    public VoteResult call() throws Exception {
                        
                                VoteParam voteParam = VoteParam
                                .builder()
                                .candateAddr(myAddr)
                                .candateLogTerm(term)
                                .candateLogIndex(getLastLogIndex())
                                .peerAddr(peer)
                                .build();

                            Request request = Request
                                .builder()
                                .requestType(RequestType.R_VOTE)
                                .param(voteParam)
                                .build();
                        try{
                            return rpcClient.send(request);
                        }catch(Exception e){
                            log.error("vote send fail: from {} to {}, error : {}", myAddr, peer, e);
                            return VoteResult.fail();
                        }finally{
                            semaphore.release();
                        }
                    }
                }));
            }

            try {
                // 一半响应就可以 因为如果可以成为节点 那么证明任何一个节点都会给投票自己
                semaphore.tryAcquire((int) Math.floor((peerAddrs.size() + 1) / 2), 6000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("acquire elections semphone interrupted e : {}", e);
            }

            int voteNum = 0;

            for (Future<VoteResult> future : futureList) {
                try {
                    VoteResult result = null;
                    if(future.isDone())
                        result = future.get();
                    else
                        continue;

                    // 如果获得选票
                    if(result.isVoteGranted())
                        voteNum++;
                    else{
                        if(result.getItem() >= term){
                            term = result.getItem();
                            nodeState = NodeState.FOLLOWER;
                        }
                    }
                    
                } catch (Exception e) {
                    log.error("election future exception: {} ", e);
                }
            }

            // 有可能appendEntries中转变成追随者 重新更新选票时间
            if(nodeState == NodeState.FOLLOWER){
                log.info("convert to follower, stop election, myAddr = {}", myAddr);
                voteFor = "";
                updatePreElectionTime();
                return;
            }

            // 获取的选票超过一半转变为leader 否则成为Follower
            if(voteNum * 2 >= peerAddrs.size()){
                nodeState = NodeState.LEADER;
                voteFor = "";
                leaderID = myAddr;
                //开启心跳任务
                heartBeatTaskFuture = se.scheduleAtFixedRate(heartBeatTask, 0, 
                                            heartBeatInterval, TimeUnit.MILLISECONDS);

                if(leaderInit()){
                    log.warn("node become leader address : {}, voteNum : {}", myAddr, voteNum);
                }else{
                    voteFor = "";
                    nodeState = NodeState.FOLLOWER;
                    updatePreElectionTime();
                    log.info("election failed : leader init failed !!!");
                }
            }else{
                voteFor = "";
                nodeState = NodeState.FOLLOWER;
                updatePreElectionTime();
                log.info("election failed : voteNum not enough !!!");
            }
        }
        
    }

    // 处理投票的请求 可能leader收到该请求
    public VoteResult requestVote(Request request){
        updatePreElectionTime();
        VoteParam voteParam = (VoteParam)request.getParam();
        try {
            voteLock.lock();
            // 判断任期 任期小 更新对方任期
            if(voteParam.getTerm() < term){
                return VoteResult.fail(term);
            }

            // 如果还没投过票 或者是之前投票的节点
            if(StringUtils.isNotEmpty(voteFor) || voteFor.equals(voteParam.getCandateAddr())){
                // 对方最后日志记录的任期和索引都没有自己新
                if(voteParam.getCandateLogTerm() < logModule.getLast().getTerm()){
                    log.info("declare to vote for candidate {}, because of older last log term !!",
                                                                    voteParam.getCandateAddr());
                    return VoteResult.fail(term);
                }
                if(voteParam.getCandateLogIndex() > getLastLogIndex()){
                    log.info("declare to vote for candidate {}, because of older last log index !!",
                                                                    voteParam.getCandateAddr());
                    return VoteResult.fail(term);
                }
            }

            //更新状态 如果是leader 需要停止心跳任务
            nodeState = NodeState.FOLLOWER;
            stopHeartBeatTask();
    
            // 同意为候选人投票
            leaderID = voteParam.getCandateAddr();
            voteFor = voteParam.getCandateAddr();
            term = voteParam.getTerm();
            log.info("agree vote for candidate : {}", voteParam.getCandateAddr());
            return VoteResult.ok();
        } catch (Exception e) {
            log.error("request vote exception : ", e);
            return VoteResult.fail(term);
        }finally{
            updatePreElectionTime();
            voteLock.unlock();
        }
    }

    public long getLastCommit(){
        return stateMachine.getLastCommit();
    }

    public long getLastLogIndex(){
        return logModule.getLastIndex();
    }

    // 更新选举时间
    private void updatePreElectionTime(){
        preElectionTime = System.currentTimeMillis() + RANDOM.nextInt(20) * 100;
    }

    private void stopHeartBeatTask() {
        if(heartBeatTaskFuture != null){
            heartBeatTaskFuture.cancel(true);
            heartBeatTaskFuture = null;
        }
    }



}
