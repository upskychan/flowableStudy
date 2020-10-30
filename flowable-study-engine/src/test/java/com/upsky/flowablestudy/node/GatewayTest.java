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

    /**
     * 网关_不配条件。
     */
    @Test
    public void deployNoCondition() {
//        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("网关测试").name("gateway_nocondition_excluside")
//                .addClasspathResource("bpmn_xml/网关_不配条件_互斥.bpmn20.xml");
//        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("网关测试").name("gateway_nocondition_inclusive")
//                .addClasspathResource("bpmn_xml/网关_不配条件_包容.bpmn20.xml");
        //互斥网关后的出线上配置默认出线，默认出线上不允许配置条件（Default sequenceflow has a condition, which is not allowed）
        //当没有符合条件的其它分支时，才会走默认出线
//        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("网关测试").name("gateway_nocondition_excluside")
//                .addClasspathResource("bpmn_xml/网关_默认出线_互斥.bpmn20.xml");
        //包容网关后的出线上配置默认出线，默认出线上可以配置条件，但会被忽视掉（即不管是否满足条件，只有当没有符合条件的其它分支时，才会走默认出线）
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("网关测试").name("gateway_nocondition_inclusive")
                .addClasspathResource("bpmn_xml/网关_默认出线_包容.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例（网关_不配条件_互斥）。
     * 结论：
     *   1、互斥网关的分支中如果存在满足条件的分支，则走该分支。
     *   2、如果不存在配了条件且满足的分支，默认走未配条件的分支。
     *   3、从第一条分支开始遍历，计算成功的出线之前的条件变量必须传，之后的条件变量可以不传。
     *   4、设置默认流的情况：
     *   4.1、互斥网关后的出线上配置默认出线，默认出线上不允许配置条件（Default sequenceflow has a condition, which is not allowed）。
     *   4.2当没有符合条件的其它分支时，才会走默认出线。
     */
    @Test
    public void startProcessInstanceByKeyNoCondition1() {
        String processDefinitionKey = "gateway_nocondition_excluside";
        String businessKey = "gateway_nocondition_excluside_5";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,businessKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 启动流程实例（网关_不配条件_包容）。
     * 结论：
     *   1、包容网关的分支中如果存在满足条件的分支，则走该分支。
     *   2、并且未配置条件的分支也会走。
     *   3、所有分支上的条件变量都必须传（并行网关的分支条件变量不用传）。
     *   4、包容网关后的出线上配置默认流，默认出线上可以配置条件，但会被忽视掉（即不管是否满足条件，只有当没有符合条件的其它分支时，才会走默认出线）。
     */
    @Test
    public void startProcessInstanceByKeyNoCondition2() {
        String processDefinitionKey = "gateway_nocondition_inclusive";
        String businessKey = "gateway_nocondition_inclusive_4";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,businessKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }
}

