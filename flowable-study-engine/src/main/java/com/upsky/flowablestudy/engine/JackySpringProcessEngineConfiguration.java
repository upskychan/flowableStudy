package com.upsky.flowablestudy.engine;

import org.flowable.spring.SpringProcessEngineConfiguration;

/**
 * 扩展SpringProcessEngineConfiguration类
 */
public class JackySpringProcessEngineConfiguration extends SpringProcessEngineConfiguration {
    @Override
    public void init() {
        System.out.println("JackySpringProcessEngineConfiguration init()!");
        super.init();
    }
}
