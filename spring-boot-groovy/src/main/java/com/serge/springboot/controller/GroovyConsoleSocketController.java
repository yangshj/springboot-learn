package com.serge.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.serge.springboot.component.ApplicationContextUtils;
import com.serge.springboot.component.GroovyShellUtil;
import com.serge.springboot.component.ScriptResult;
import com.serge.springboot.component.WorkThread;
import com.serge.springboot.component.WorkThreadUtil;
import com.serge.springboot.pojo.City;
import com.serge.springboot.service.CityService;
import groovy.lang.GroovyShell;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 用长链接的方式，实现事务控制。
 * 1、动态执行代码，有风险
 * 2、添加事务后，可以查看自己执行结果是否正确后，再进行提交代码或者回滚
 */
@Controller
@RequestMapping("/consoleSocket")
public class GroovyConsoleSocketController implements ApplicationContextAware {


    @Autowired
    private CityService cityService;


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
//        return testTransaction(script);
        if(!WorkThreadUtil.workThreadMap.containsKey(userId)){
            WorkThread workThread = WorkThreadUtil.createThread(userId);
            workThread.start();
            return workThread.execute(script);
        } else {
            WorkThread workThread = WorkThreadUtil.workThreadMap.get(userId);
            return workThread.execute(script);
        }
    }


    // 测试事务
    public ScriptResult testTransaction(String script){
        PlatformTransactionManager platformTransactionManager = ApplicationContextUtils.getBean(PlatformTransactionManager.class);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        TransactionStatus status = platformTransactionManager.getTransaction(def); // 获得事务状态
        City city = new City();
        city.setCityName("北京");
        city.setDescription("北京是首都");
        city.setProvinceId(1L);
        cityService.insert(city);
        List<City> list =  cityService.findAll();
        System.out.println(JSON.toJSONString(list));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GroovyShell groovyShell = GroovyShellUtil.createGroovyShell(ApplicationContextUtils.getApplicationContext(), out);
        Object result = groovyShell.evaluate(script);
        ScriptResult scriptResult =  ScriptResult.create(result, out.toString());
        platformTransactionManager.rollback(status);
        return scriptResult;
    }

        // 以下代码可以粘贴到控制台直接执行
    public void testConsole(String script){
//        import com.serge.springboot.pojo.City;
//        def cityService = applicationContext.getBean("cityService")
//        City city = new City();
//        city.setCityName("北京");
//        city.setDescription("北京是首都");
//        city.setProvinceId(1L);
//        cityService.insert(city);
//        List<City> list =  cityService.findAll();
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.setApplicationContext(applicationContext);
    }




}
