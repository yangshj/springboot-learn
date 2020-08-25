package com.serge.springboot.component;

import groovy.lang.GroovyShell;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
    private PlatformTransactionManager platformTransactionManager;

    // 最后一次脚本执行结果
    private ScriptResult scriptResult;
    // 所有执行结果
    private List<ScriptResult> resultList = new ArrayList<>();

    private Object lock = new Object();

    public WorkThread(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
        this.platformTransactionManager = ApplicationContextUtils.getBean(PlatformTransactionManager.class);
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            try {
                String script = queue.poll(30, TimeUnit.SECONDS);
                if(script==null){
                    continue;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GroovyShell groovyShell = GroovyShellUtil.createGroovyShell(applicationContext, out);
                Object result = groovyShell.evaluate(script);
                scriptResult =  ScriptResult.create(result, out.toString());
                resultList.add(scriptResult);
                synchronized (lock) {
                    lock.notifyAll();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public ScriptResult execute(String script){
        queue.add(script);
        try {
            synchronized (lock){
                lock.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scriptResult;
    }


    public void beginTransaction(){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        transactionStatus = platformTransactionManager.getTransaction(def); // 获得事务状态
        System.out.println("开启事务");
    }

    public void commitTransaction(){
        if(transactionStatus!=null){
            platformTransactionManager.commit(transactionStatus);
            System.out.println("提交事务");
        }
    }

    public void rollBackTransaction(){
        if(transactionStatus!=null) {
            platformTransactionManager.rollback(transactionStatus);
            System.out.println("回滚事务");
        }
    }





}
