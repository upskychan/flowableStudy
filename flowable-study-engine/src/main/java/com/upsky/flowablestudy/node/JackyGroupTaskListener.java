package com.upsky.flowablestudy.node;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务监听器，任务创建时设置任务办理人。
 */
public class JackyGroupTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
//        delegateTask.addCandidateUser(userId);
        List<String> userList = new ArrayList<>();
        userList.add("陈杰7");
        userList.add("陈杰8");
        delegateTask.addCandidateUsers(userList);
    }
}
