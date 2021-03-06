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

import java.io.*;

/**
 * Controller for evaluating scripts from groovy console.
 */
@Controller
@RequestMapping("/console")
public class GroovyConsoleController implements ApplicationContextAware {


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
