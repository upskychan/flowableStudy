package com.upsky.flowablestudy.node;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 任务监听器，任务创建时设置任务办理人。
 */
public class JackySingleTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("Jacky");
    }
}
