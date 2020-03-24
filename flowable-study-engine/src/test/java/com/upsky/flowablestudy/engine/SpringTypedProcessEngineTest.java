package com.upsky.flowablestudy.engine;

import org.flowable.engine.*;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * spring风格的配置
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class SpringTypedProcessEngineTest {
    ProcessEngine processEngine;

    @Test
    public void testSpringTypedProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.print("流程引擎类：" + processEngine);
        System.out.println(",class:" + processEngine.getClass());

        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        System.out.println("ProcessEngineConfiguration:" + processEngineConfiguration);

        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
        FormService formService = processEngine.getFormService();
        HistoryService historyService = processEngine.getHistoryService();
        IdentityService identityService = processEngine.getIdentityService();
        ManagementService managementService = processEngine.getManagementService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        System.out.println("dynamicBpmnService:" + dynamicBpmnService);
        System.out.println("formService:" + formService);
        System.out.println("historyService:" + historyService);
        System.out.println("identityService:" + identityService);
        System.out.println("managementService:" + managementService);
        System.out.println("repositoryService:" + repositoryService);
        System.out.println("runtimeService:" + runtimeService);
        System.out.println("taskService:" + taskService);

        List<Task> taskList = taskService.createTaskQuery().list();
        if (taskList != null) {
            for (Task task : taskList) {
                System.out.println("TaskName:" + task.getName());
            }
        }
    }
}
