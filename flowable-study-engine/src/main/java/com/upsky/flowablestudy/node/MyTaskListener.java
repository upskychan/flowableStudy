package com.upsky.flowablestudy.node;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 任务监听器，节点上任务创建、指派、完成、删除时触发。
 * 任务监听器的事件类型包括：
 * create（任务创建）
 * assignment（指派参与人，先与create事件触发）
 * complete（任务完成）
 * delete（任务删除）
 */
@Component("myTaskListener")
public class MyTaskListener implements TaskListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("进入MyTaskListener.notify()方法");
        logger.info("EventName:{}", delegateTask.getEventName());
        logger.info("当前节点：{}", delegateTask.getName());
        logger.info("审核人：{}", delegateTask.getAssignee());
    }
}
