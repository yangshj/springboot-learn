package com.serge.springboot.controller;

import com.serge.springboot.component.GroovyShellUtil;
import com.serge.springboot.component.ScriptResult;
import com.serge.springboot.component.WorkThread;
import com.serge.springboot.pojo.City;
import com.serge.springboot.service.CityService;
import groovy.lang.GroovyShell;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 用长链接的方式，实现事务控制。
 * 1、动态执行代码，有风险
 * 2、添加事务后，可以查看自己执行结果是否正确后，再进行提交代码或者回滚
 */
@Controller
@RequestMapping("/consoleSocket")
public class GroovyConsoleSocketController implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private CityService cityService;
    // 工作线程集合
    private Map<String, WorkThread> workThreadMap = new HashMap<String, WorkThread>();

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/console/socketIndex.html";
    }



    @RequestMapping(value = "/groovy", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ScriptResult execute(@RequestParam String userId, @RequestParam String script) {
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(script)){
            return ScriptResult.create("非法请求",null);
        }
        if(!workThreadMap.containsKey(userId)){
            WorkThread workThread = new WorkThread(applicationContext);
            workThreadMap.put(userId, workThread);
            return workThread.execute(script);
        } else {
            WorkThread workThread = workThreadMap.get(userId);
            return workThread.execute(script);
        }
//        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
//        platformTransactionManager.commit(transactionStatus);
//        City city = new City();
//        city.setCityName("北京");
//        city.setDescription("北京是首都");
//        city.setProvinceId(1L);
//        cityService.insert(city);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        GroovyShell groovyShell = GroovyShellUtil.createGroovyShell(applicationContext, out);
//        Object result = groovyShell.evaluate(script);
//        ScriptResult scriptResult =  ScriptResult.create(result, out.toString());
//        platformTransactionManager.rollback(transactionStatus);
//        return scriptResult;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }




}
