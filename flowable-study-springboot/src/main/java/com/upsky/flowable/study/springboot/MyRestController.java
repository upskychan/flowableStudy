package com.upsky.flowable.study.springboot;

import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package: com.upsky.flowable.study.springboot
 * @Author: Upsky
 * @CreateTime: 2020/10/29 15:44
 * @Description:
 */
@RestController
public class MyRestController {
    @Autowired
    private MyService myService;

    @RequestMapping(value = "start", method = RequestMethod.GET)
    public boolean startProcessInstance() {
        return myService.startProcess();
    }

    @RequestMapping(value = "count", method = RequestMethod.GET)
    public long taskCount() {
        return myService.ruTaskCount();
    }

    @RequestMapping(value = "tasks", method = RequestMethod.GET)
    public List<Map<String, String>> getTasks(@RequestParam String user) {
        List<Map<String, String>> taskList = new ArrayList<>();
        List<Task> tasks = myService.getTasks(user);
        if (tasks != null) {
            for (Task task : tasks) {
                Map<String, String> taskInfo = new HashMap<>();
                taskInfo.put("id", task.getId());
                taskInfo.put("name", task.getName());
                taskInfo.put("assignee", task.getAssignee());
                taskInfo.put("times", task.getCreateTime().toLocaleString());
                taskList.add(taskInfo);
            }
        }
        return taskList;
    }

}
