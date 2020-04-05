package com.upsky.flowablestudy.procinst;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ProcessInstanceTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;


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
        runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

//        System.out.println("dynamicBpmnService:" + dynamicBpmnService);
//        System.out.println("formService:" + formService);
//        System.out.println("historyService:" + historyService);
//        System.out.println("identityService:" + identityService);
//        System.out.println("managementService:" + managementService);
//        System.out.println("repositoryService:" + repositoryService);
//        System.out.println("runtimeService:" + runtimeService);
//        System.out.println("taskService:" + taskService);
    }

    /**
     * Classpath资源部署方式。
     */
    @Test
    public void deployByClasspath() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .category("流程实例学习分类")
                .name("部署名称-简易请假流程")
                .key("holidayeasy")
                .tenantId("805")
                .addClasspathResource("简易请假流程.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 删除部署的流程定义。
     */
    @Test
    public void testDeleteDeployment() {
        String deploymentId = "15001";//act_re_deployment表对应的ID
        try {
            //如果当前流程定义下有正在执行的流程，则抛异常。
            repositoryService.deleteDeployment(deploymentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动流程实例。
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "holidayeasy";
        String tenantId = "805";
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey,tenantId);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

}
