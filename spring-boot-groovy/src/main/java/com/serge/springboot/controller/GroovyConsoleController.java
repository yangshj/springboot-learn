package com.serge.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;

/**
 * Controller for evaluating scripts from groovy console.
 */
@Controller
@RequestMapping("/console")
public class GroovyConsoleController implements ApplicationContextAware {

    private static final long SCRIPT_TIMEOUT_IN_SECONDS = 5;
    private static final List<String> RECEIVERS_BLACK_LIST = Stream.of(System.class, Thread.class)
            .map(Class::getName)
            .collect(Collectors.toList());

    private ApplicationContext applicationContext;

    /**
     * Redirects to groovy console index page.
     *
     * @return the redirect view of groovy console index page
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/console/index.html";
    }

    /**
     * Executes the given groovy script
     *
     * @param script the groovy script
     * @return the result object
     */
    @RequestMapping(value = "/groovy", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ScriptResult execute(@RequestParam String script) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GroovyShell groovyShell = createGroovyShell(out);
        Object result = groovyShell.evaluate(script);
        ScriptResult scriptResult =  ScriptResult.create(result, out.toString());
        return scriptResult;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private GroovyShell createGroovyShell(OutputStream outputStream) {
        CompilerConfiguration configuration = createCompilerConfiguration();
        Binding binding = createBinding(outputStream);
        return new GroovyShell(binding, configuration);
    }

    private Binding createBinding(OutputStream outputStream) {
        Binding binding = new Binding();
        binding.setVariable("applicationContext", applicationContext);
        Map<String, Object> beanMap = applicationContext.getBeansOfType(Object.class);
        //遍历设置所有bean,可以根据需求在循环中对bean做过滤
        for (String beanName : beanMap.keySet()) {
            binding.setVariable(beanName, beanMap.get(beanName));
        }
        binding.setProperty("out", new PrintStream(outputStream, true));
//        binding.setProperty("out",System.out);
//        binding.setProperty("stdin",System.out);
//        binding.setProperty("stdout",System.out);
//        binding.setProperty("stderr",System.err);
        return binding;
    }

    private CompilerConfiguration createCompilerConfiguration() {
        ASTTransformationCustomizer timedCustomizer = new ASTTransformationCustomizer(singletonMap("value", SCRIPT_TIMEOUT_IN_SECONDS), TimedInterrupt.class);
        // 安全控制
        SecureASTCustomizer secureCustomizer = new SecureASTCustomizer();
//        secureCustomizer.setReceiversBlackList(RECEIVERS_BLACK_LIST);
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(secureCustomizer, timedCustomizer);
        return configuration;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static final class ScriptResult {

        private String[] output;
        private Object result;

        private ScriptResult() {
        }

        public String[] getOutput() {
            return output;
        }

        public Object getResult() {
            return result;
        }

        private static ScriptResult create(Throwable throwable) {
            String message = throwable.getMessage() == null ? throwable.getClass().getName() : throwable.getMessage();
            return create(null, message);
        }

        private static ScriptResult create(Object result, String output) {
            ScriptResult scriptletResult = new ScriptResult();
            scriptletResult.result = result;
            if (StringUtils.hasLength(output)) {
                scriptletResult.output = output.split(System.lineSeparator());
            }
            return scriptletResult;
        }
    }
}
