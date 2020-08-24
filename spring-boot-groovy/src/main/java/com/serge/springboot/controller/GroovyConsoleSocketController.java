package com.serge.springboot.controller;

import com.serge.springboot.component.GroovyShellUtil;
import com.serge.springboot.component.ScriptResult;
import groovy.lang.GroovyShell;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;

/**
 * 用长链接的方式，实现事务控制。
 * 1、动态执行代码，有风险
 * 2、添加事务后，可以查看自己执行结果是否正确后，再进行提交代码或者回滚
 */
@Controller
@RequestMapping("/consoleSocket")
public class GroovyConsoleSocketController implements ApplicationContextAware {



    private ApplicationContext applicationContext;


    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/console/index.html";
    }



    @RequestMapping(value = "/groovy", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ScriptResult execute(@RequestParam String script) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GroovyShell groovyShell = GroovyShellUtil.createGroovyShell(applicationContext, out);
        Object result = groovyShell.evaluate(script);
        ScriptResult scriptResult =  ScriptResult.create(result, out.toString());
        return scriptResult;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }




}
