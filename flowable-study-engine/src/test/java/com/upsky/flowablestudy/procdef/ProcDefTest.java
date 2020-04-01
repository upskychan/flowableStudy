package com.upsky.flowablestudy.procdef;

import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ProcDefTest {
    ProcessEngine processEngine;
    RepositoryService repositoryService;

    @Before
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
        repositoryService = processEngine.getRepositoryService();
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
    }

    /**
     * DeploymentBuilder构造方法。
     */
    @Test
    public void testDeploymentBuild() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("名称");
        System.out.println(deploymentBuilder);
    }

    /**
     * Classpath资源部署方式。
     */
    @Test
    public void deployByClasspath() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").addClasspathResource("员工请假流程模型.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 字符串方式部署（默认是按照UTF-8编码处理）。
     * 资源的名称必须是String[] { "bpmn20.xml", "bpmn" } 结尾的才可以部署到流程定义表。
     */
    @Test
    public void deployByString() {
        String text = IoUtil.readFileAsString("员工请假流程模型.bpmn20.xml");
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").key("测试的key").addString("员工请假.bpmn", text);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }


}
