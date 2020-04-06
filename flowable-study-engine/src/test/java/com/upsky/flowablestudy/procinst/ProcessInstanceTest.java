package com.upsky.flowablestudy.procinst;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ProcessInstanceTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private TaskService taskService;


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
        taskService = processEngine.getTaskService();

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

    /**
     * 查询待办任务。
     * SQL:select distinct RES.* from ACT_RU_TASK RES inner join ACT_RE_PROCDEF D on RES.PROC_DEF_ID_ = D.ID_ WHERE RES.ASSIGNEE_ = ? and D.KEY_ = ? order by RES.ID_ asc
     * Parameters: 张三(String), holidayeasy(String)
     */
    @Test
    public void queryRuTask() {
        String processDefinitionKey = "holidayeasy";
        String assignee = "张三";//指定个人任务办理人
        assignee = "李四";
        List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskAssignee(assignee).list();
        if (taskList != null) {
            System.out.println("一共查询到" + taskList.size() + "条待办任务：");
            for (Task task : taskList) {
                System.out.println("**************" + task.getAssignee() + "的待办任务******************");
                System.out.println(task.getId());
                System.out.println(task.getName());
                System.out.println(task.getTaskDefinitionKey());
                System.out.println(task.getExecutionId());
                System.out.println(task.getProcessInstanceId());
                System.out.println(task.getProcessDefinitionId());
                System.out.println(task.getCreateTime());
            }
        }
    }

    /**
     * 审核任务。
     * 注意看执行了哪些SQL。
     */
    @Test
    public void completeTask() {
        String taskId = "45006";
        taskService.complete(taskId);
    }

    /**
     * 查询流程实例。
     * SQL:select distinct RES.* , P.KEY_ as ProcessDefinitionKey, P.ID_ as ProcessDefinitionId, P.NAME_ as ProcessDefinitionName, P.VERSION_ as ProcessDefinitionVersion, P.DEPLOYMENT_ID_ as DeploymentId from ACT_RU_EXECUTION RES inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_ WHERE RES.PARENT_ID_ is null and RES.ID_ = ? and RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     * 关键点：查询表ACT_RU_EXECUTION RES，条件PARENT_ID_ is null
     */
    @Test
    public void queryProcessInstanceState() {
        String procInstId = "35001";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (processInstance != null) {
            System.out.println("当前的流程实例正在运行");
        } else {
            System.out.println("当前的流程实例已经结束");
        }
    }

    /**
     * 查询执行实例。
     * SQL:select distinct RES.* , P.KEY_ as ProcessDefinitionKey, P.ID_ as ProcessDefinitionId from ACT_RU_EXECUTION RES inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_ WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     * 关键点：流程实例也是其中一条执行实例，流程实例的execution.getActivityId()为null。
     */
    @Test
    public void queryExecution() {
        String procInstId = "35001";
        List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(procInstId).list();
        if (executionList != null) {
            System.out.println("一共查询到" + executionList.size() + "条执行实例：");
            for (Execution execution : executionList) {
                System.out.println(execution.getId() + "," + execution.getActivityId());
            }
        }
    }

}
