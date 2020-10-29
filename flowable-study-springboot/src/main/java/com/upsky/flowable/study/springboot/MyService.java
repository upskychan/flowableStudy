package com.upsky.flowable.study.springboot;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Package: com.upsky.flowable.study.springboot
 * @Author: Upsky
 * @CreateTime: 2020/10/29 15:34
 * @Description:
 */
@Service
public class MyService {
    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Transactional
    public boolean startProcess() {
        String processDefinitionKey = "gateway_nocondition_inclusive";
        String businessKey = "gateway_nocondition_inclusive_" + System.currentTimeMillis();
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
            logger.info("{}的流程实例ID：{},{}", businessKey, processInstance.getId(), processInstance.getActivityId());

            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            if (taskList != null) {
                for (Task task : taskList) {
                    taskService.claim(task.getId(), "陈杰");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Transactional
    public List<Task> getTasks(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }

    public long ruTaskCount() {
        return taskService.createTaskQuery().count();
    }
}
