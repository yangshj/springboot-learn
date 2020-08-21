package com.serge.springboot.service;

import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author : 杨帅军 (shuaijun.yang@ucarinc.com)
 * @since : 2020/8/21 17:55:24
 **/
@Service
public class TestService {

    private String testQuery(long id){
        return "Test query success, id is " + id;
    }
}
