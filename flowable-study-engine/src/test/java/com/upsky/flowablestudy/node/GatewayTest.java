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
 * 网关测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class GatewayTest {
    ProcessEngine processEngine;
    RepositoryService repositoryService;
    RuntimeService runtimeService;
    TaskService taskService;
    HistoryService historyService;
    IdentityService identityService;
    ManagementService managementService;

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

    /**
     * （排他网关）
     */
    @Test
    public void deploy() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("网关测试").name("gatewayTest")
                .addClasspathResource("bpmn_xml/网关_排他网关.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例（排他网关）
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "ExclusideGateway";
        Map<String,Object> vars=new HashMap<String, Object>();
        vars.put("day",2);
//        vars.put("day",4);
        vars.put("day",6);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,vars);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    @Test
    public void complete() {
        taskService.complete("32507");
    }

    /**
     * （并行网关）
     */
    @Test
    public void deployParallelGataWay() {
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("并行网关")
                .addClasspathResource("bpmn_xml/网关_并行网关.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例（并行网关）
     */
    @Test
    public void startProcessInstanceByKeyParallelGataWay() {
        String processDefinitionKey = "ParallelGataWay";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * （包容网关）
     */
    @Test
    public void deployInclusiveGateway() {
        DeploymentBuilder deploymentBuilder = repositoryService
                .createDeployment()
                .category("兼容网关")
                .addClasspathResource("bpmn_xml/网关_兼容网关.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例（包容网关）
     */
    @Test
    public void startProcessInstanceByKeyInclusiveGateway() {
        String processDefinitionKey = "inclusiveGateway";
        Map<String,Object> vars=new HashMap<String, Object>();
        vars.put("money",300);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,vars);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }
}

