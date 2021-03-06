package com.upsky.flowablestudy.node;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 脚本任务测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ScriptTaskTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private IdentityService identityService;
    private ManagementService managementService;

    @Before
    public void buildProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
        repositoryService = processEngine.getRepositoryService();
        System.out.println(repositoryService);
        String name = processEngine.getName();
        System.out.println("流程引擎的名称：" + name);
        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
        System.out.println(dynamicBpmnService);
        FormService formService = processEngine.getFormService();
        System.out.println(formService);

        historyService = processEngine.getHistoryService();
        System.out.println(historyService);
        identityService = processEngine.getIdentityService();
        System.out.println(identityService);
        managementService = processEngine.getManagementService();
        System.out.println(managementService);
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        System.out.println(processEngineConfiguration);
        runtimeService = processEngine.getRuntimeService();
        System.out.println(runtimeService);
        taskService = processEngine.getTaskService();
        System.out.println(taskService);
    }

    @Test
    public void deploy() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("脚本任务").name("scriptTask")
//                .addClasspathResource("bpmn_xml/脚本任务测试.bpmn20.xml");
//                 .addClasspathResource("bpmn_xml/脚本任务测试2.bpmn20.xml");
                .addClasspathResource("bpmn_xml/脚本任务测试3.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "scriptTask";
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("a", 10);
        vars.put("b", 5);
        vars.put("echo", "陈杰");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, vars);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey2() {
        String processDefinitionKey = "scriptTask";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey3() {
        String processDefinitionKey = "scriptTask";
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("echo", "陈杰");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, vars);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }
}

