package com.serge.springboot.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.serge.springboot.component.WorkThread;
import com.serge.springboot.component.WorkThreadUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@ServerEndpoint("/webSocket/{sid}")
@Component
public class WebSocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //线程安全Map，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                System.out.println("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }

    //给指定用户发送信息
    public void sendInfo(String userName, String message){
        Session session = sessionPools.get(userName);
        try {
            sendMessage(session, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String userName){
        sessionPools.put(userName, session);
        addOnlineCount();
        System.out.println(userName + "加入webSocket！当前人数为" + onlineNum);
        try {
            sendMessage(session, "开启链接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "sid") String userName){
        sessionPools.remove(userName);
        subOnlineCount();
        System.out.println(userName + "断开webSocket连接！当前人数为" + onlineNum);
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(Session session, String message) throws IOException{
        System.out.println(message);
        JSONObject  jsonObject = JSON.parseObject(message);
        String userId = (String) jsonObject.get("userId");
        if(!sessionPools.containsKey(userId)){
            System.out.println("非法请求: " + message);
        }
        String messageStr = (String)jsonObject.get("message");
        if(!WorkThreadUtil.workThreadMap.containsKey(userId)){
            WorkThread workThread = WorkThreadUtil.createThread(userId);
            workThread.start();
        }
        if(messageStr.equals("beginTransaction")){
            WorkThread workThread = WorkThreadUtil.workThreadMap.get(userId);
            workThread.beginTransaction();
        } else if(messageStr.equals("commitTransaction")){
            WorkThread workThread = WorkThreadUtil.workThreadMap.get(userId);
            workThread.commitTransaction();
        } else if(messageStr.equals("rollBackTransaction")){
            WorkThread workThread = WorkThreadUtil.workThreadMap.get(userId);
            workThread.rollBackTransaction();
        } else {
            System.out.println("非法指令: " + message);
        }
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

}
