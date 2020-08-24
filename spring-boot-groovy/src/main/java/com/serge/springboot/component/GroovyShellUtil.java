package com.serge.springboot.component;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;

/**
 * Description:
 *
 * @author : 杨帅军 (shuaijun.yang@ucarinc.com)
 * @since : 2020/8/24 18:36:22
 **/
public class GroovyShellUtil {

    private static final long SCRIPT_TIMEOUT_IN_SECONDS = 5;
    private static final List<String> RECEIVERS_BLACK_LIST = Stream.of(System.class, Thread.class)
            .map(Class::getName)
            .collect(Collectors.toList());

    public static GroovyShell createGroovyShell(ApplicationContext applicationContext, OutputStream outputStream){
        CompilerConfiguration configuration = createCompilerConfiguration();
        Binding binding = createBinding(applicationContext, outputStream);
        return new GroovyShell(binding, configuration);
    }

    private static Binding createBinding(ApplicationContext applicationContext, OutputStream outputStream) {
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

    private static CompilerConfiguration createCompilerConfiguration() {
        ASTTransformationCustomizer timedCustomizer = new ASTTransformationCustomizer(singletonMap("value", SCRIPT_TIMEOUT_IN_SECONDS), TimedInterrupt.class);
        // 安全控制
        SecureASTCustomizer secureCustomizer = new SecureASTCustomizer();
//        secureCustomizer.setReceiversBlackList(RECEIVERS_BLACK_LIST);
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.addCompilationCustomizers(secureCustomizer, timedCustomizer);
        return configuration;
    }

}
