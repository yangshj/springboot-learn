package com.serge.springboot.component;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 *
 * @author : 杨帅军 (shuaijun.yang@ucarinc.com)
 * @since : 2020/8/25 11:06:50
 **/
public class WorkThreadUtil {

    // 工作线程集合
    public static Map<String, WorkThread> workThreadMap = new HashMap<String, WorkThread>();

    /**
     * 创建线程
     */
    public static WorkThread createThread(String userId){
        PlatformTransactionManager platformTransactionManager = ApplicationContextUtils.getBean(PlatformTransactionManager.class);
        TransactionDefinition transactionDefinition =  ApplicationContextUtils.getBean(TransactionDefinition.class);
        WorkThread workThread = new WorkThread(ApplicationContextUtils.getApplicationContext(), platformTransactionManager, transactionDefinition);
        workThreadMap.put(userId, workThread);
        return workThread;
    }
}
