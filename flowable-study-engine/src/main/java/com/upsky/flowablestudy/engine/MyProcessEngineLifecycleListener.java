package com.upsky.flowablestudy.engine;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineLifecycleListener;

/**
 * 自定义流程引擎监听器
 */
public class MyProcessEngineLifecycleListener implements ProcessEngineLifecycleListener {
    /**
     * 流程引擎创建时会调用该方法
     *
     * @param processEngine
     */
    @Override
    public void onProcessEngineBuilt(ProcessEngine processEngine) {
        System.out.println("自定义流程引擎监听器MyProcessEngineLifecycleListener.onProcessEngineBuilt");
    }

    /**
     * 流程引擎关闭时会调用该方法
     *
     * @param processEngine
     */
    @Override
    public void onProcessEngineClosed(ProcessEngine processEngine) {
        System.out.println("自定义流程引擎监听器MyProcessEngineLifecycleListener.onProcessEngineClosed");
    }
}
