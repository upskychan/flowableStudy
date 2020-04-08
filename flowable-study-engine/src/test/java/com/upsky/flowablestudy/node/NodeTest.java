package com.upsky.flowablestudy.node;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 节点。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class NodeTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private IdentityService identityService;
    private ManagementService managementService;
    private FormService formService;
    private DynamicBpmnService dynamicBpmnService;

    @Before
    public void testSpringTypedProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.print("流程引擎类：" + processEngine);
        System.out.println(",class:" + processEngine.getClass());

        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        System.out.println("ProcessEngineConfiguration:" + processEngineConfiguration);

        dynamicBpmnService = processEngine.getDynamicBpmnService();
        formService = processEngine.getFormService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
    }

    /**
     * Classpath资源部署方式（接收任务）。
     */
    @Test
    public void deployByClasspath() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .category("流程实例学习分类")
                .name("部署名称-接收任务测试")
//                .key("receivetask")
                .addClasspathResource("接收任务测试.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 通过流程定义Key和业务Key启动流程实例。
     * 接收任务启动后ACT_RU_TASK表无待办任务，但是ACT_RU_EXECUTION表有执行实例，有活动节点。
     */
    @Test
    public void startProcessInstanceByKeyAndBusinessKey() {
        String processDefinitionKey = "receivetask";
        String businessKey = "businessKey-receivetask";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 通过活动节点ID查询执行实例。
     */
    @Test
    public void testQueryExecutionActivityId() {
        String activityId = "sid-43D1D00A-A9A6-4D4E-88CF-7CA2CCC359FD";//第一个节点
//        activityId = "sid-88B3ACC4-E163-41F4-8B1B-31F4978ED9D8";//第二个节点
        Execution execution = runtimeService.createExecutionQuery().activityId(activityId).singleResult();
        System.out.println(execution.getId());
    }

    /**
     * 触发执行实例继续往下运转。
     */
    @Test
    public void trigger() {
        String executionId = "62502";//必须填写执行实例的ID，不能使用流程实例ID
        runtimeService.trigger(executionId);
    }
}