package com.serge.distributedlock.service;

import com.serge.distributedlock.annotations.DistributeLock;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * <p></p>
 *
 * @author Ant丶
 * @date 2018-05-30.
 */
@Service
public class TestService {


    @DistributeLock(name = "TestService_distributedLockTest", value = "#key.concat(#value)")
    public String distributedLockTest(String key, String value) throws InterruptedException {

        Thread.sleep(5000L);
        return "distributedLockTest";

    }
}
