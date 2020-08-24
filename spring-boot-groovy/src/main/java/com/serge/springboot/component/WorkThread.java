package com.serge.springboot.component;

import groovy.lang.GroovyShell;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 工作线程
 */
public class WorkThread extends Thread {

    // spring上下文
    private ApplicationContext applicationContext;
    // 待执行的脚本任务队列
    private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    // 事务
    private TransactionStatus transactionStatus;
    // 最后一次脚本执行结果
    private ScriptResult scriptResult;
    // 所有执行结果
    private List<ScriptResult> resultList = new ArrayList<>();

    private Object lock = new Object();

    public WorkThread(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        while (isInterrupted()){
            try {
                String script = queue.poll(30, TimeUnit.SECONDS);
                if(script==null){
                    continue;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GroovyShell groovyShell = GroovyShellUtil.createGroovyShell(applicationContext, out);
                Object result = groovyShell.evaluate(script);
                scriptResult =  ScriptResult.create(result, out.toString());
                lock.notify();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public ScriptResult execute(String script){
        queue.add(script);
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return scriptResult;
    }


    public void beginTransaction(PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition){
        transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
    }

    public void commitTransaction(PlatformTransactionManager platformTransactionManager){
        if(transactionStatus!=null){
            platformTransactionManager.commit(transactionStatus);
        }
    }

    public void rollBackTransaction(PlatformTransactionManager platformTransactionManager){
        if(transactionStatus!=null) {
            platformTransactionManager.rollback(transactionStatus);
        }
    }





}
