package com.serge.springboot.component;


import groovy.lang.Script;

/**
 * Description:
 *
 * @author : 杨帅军 (shuaijun.yang@ucarinc.com)
 * @since : 2020/8/21 18:01:58
 **/
public class TestScript extends Script {
    @Override
    public Object run() {
        System.out.println("run 方法运行");
        return null;
    }

    public Integer add (Integer first, Integer second) {
        return first + second;
    }
}
