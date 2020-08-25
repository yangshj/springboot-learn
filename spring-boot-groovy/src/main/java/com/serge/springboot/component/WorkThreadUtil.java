package com.serge.springboot.component;


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
        WorkThread workThread = new WorkThread(ApplicationContextUtils.getApplicationContext());
        workThreadMap.put(userId, workThread);
        return workThread;
    }
}
