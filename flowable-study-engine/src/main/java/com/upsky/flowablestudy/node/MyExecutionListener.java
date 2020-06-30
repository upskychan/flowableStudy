package com.upsky.flowablestudy.node;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 执行监听器，流程实例流转到节点或结束，或者经过连线时触发。
 * 事件类型包括：
 * start(执行实例运行到节点时触发)
 * end(执行实际从当前节点流转到下一个节点)
 * take(连线上触发)
 */
@Component("myExecutionListener")
public class MyExecutionListener implements ExecutionListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void notify(DelegateExecution execution) {
        logger.info("进入MyExecutionListener.notify()方法");
        logger.info("EventName:{}", execution.getEventName());
        logger.info("当前节点：{}", execution.getCurrentFlowElement().getName());
    }
}
