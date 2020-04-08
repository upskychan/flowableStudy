package com.upsky.flowablestudy.procinst;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.DataObject;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程是运转测试。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ProcessInstanceTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private IdentityService identityService;
    private ManagementService managementService;
    private FormService formService;


    @Before
    public void testSpringTypedProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.print("流程引擎类：" + processEngine);
        System.out.println(",class:" + processEngine.getClass());

        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        System.out.println("ProcessEngineConfiguration:" + processEngineConfiguration);

        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
        formService = processEngine.getFormService();
        historyService = processEngine.getHistoryService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
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

    /**
     * 查询历史流程实例。
     * SQL:select distinct RES.* , DEF.KEY_ as PROC_DEF_KEY_, DEF.NAME_ as PROC_DEF_NAME_, DEF.VERSION_ as PROC_DEF_VERSION_, DEF.DEPLOYMENT_ID_ as DEPLOYMENT_ID_ from ACT_HI_PROCINST RES left outer join ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_ WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     */
    @Test
    public void testHistoricProcessInstanceQuery() {
        String processInstanceId = "35001";
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance != null) {
            System.out.println("流程实例ID：" + historicProcessInstance.getId());
            System.out.println("流程定义ID：" + historicProcessInstance.getProcessDefinitionId());
            System.out.println("流程实例开始节点：" + historicProcessInstance.getStartActivityId());
            System.out.println("流程实例结束节点：" + historicProcessInstance.getEndActivityId());
            System.out.println("流程开始时间：" + historicProcessInstance.getStartTime());
            System.out.println("流程结束时间：" + historicProcessInstance.getEndTime());
        }
    }

    /**
     * 查询流程实例状态。
     */
    @Test
    public void queryProcessInstanceState2() {
        String processInstanceId = "35001";
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance != null) {
            if (historicProcessInstance.getEndTime() == null) {
                System.out.println("当前的流程实例正在运行");
            } else {
                System.out.println("当前的流程实例已经结束");
            }
        } else {
            System.out.println("未查到流程实例ID" + processInstanceId + "对应流程！");
        }
    }

    /**
     * 查询历史活动信息。
     * SQL:select RES.* from ACT_HI_ACTINST RES order by START_TIME_ asc
     */
    @Test
    public void testHistoricActivityInstanceQuery() {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().asc().list();
        for (HistoricActivityInstance hai : list) {
            System.out.println(hai.getId() + "\t" + hai.getActivityName());
        }
    }

    /**
     * 查询历史任务信息。
     * SQL:select distinct RES.* from ACT_HI_TASKINST RES order by RES.ID_ asc
     */
    @Test
    public void testHistoricTaskInstanceQuery() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
        for (HistoricTaskInstance hiTask : list) {
            System.out.println(hiTask.getId() + "\t" + hiTask.getName() + "\t" + hiTask.getAssignee());
        }
    }

    /**
     * 设置流程实例的启动人。
     */
    @Test
    public void setAuthenticatedUserId1() {
        String authenticatedUserId = "Jacky";
        identityService.setAuthenticatedUserId(authenticatedUserId);
        String processDefinitionKey = "holidayeasy";
        String tenantId = "805";
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, tenantId);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 设置流程实例的启动人。
     */
    @Test
    public void setAuthenticatedUserId2() {
        String authenticatedUserId = "Jacky2";
        Authentication.setAuthenticatedUserId(authenticatedUserId);
        String processDefinitionKey = "holidayeasy";
        String tenantId = "805";
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, tenantId);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 获取指定流程实例的指定变量。
     * SQL:select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and NAME_= ? and TASK_ID_ is null
     * Parameters: 77001(String), day(String)
     */
    @Test
    public void getDataObject() {
        String executionId = "77001";
        String dataObject = "天数";
        DataObject dataObject1 = runtimeService.getDataObject(executionId, dataObject);
        if (dataObject1 != null) {
            System.out.println(dataObject1.getDataObjectDefinitionKey());
            System.out.println(dataObject1.getDescription());
            System.out.println(dataObject1.getExecutionId());
            System.out.println(dataObject1.getName());
            System.out.println(dataObject1.getValue());
            System.out.println(dataObject1.getType());
        }
    }

    /**
     * 获取指定流程实例的所有变量。
     * SQL:select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
     */
    @Test
    public void getDataObject2() {
        String executionId = "77001";
        Map<String, DataObject> dataObject1 = runtimeService.getDataObjects(executionId);
        Set<Map.Entry<String, DataObject>> entries = dataObject1.entrySet();
        for (Map.Entry<String, DataObject> dataObjectEntry : entries) {
            DataObject dataObject = dataObjectEntry.getValue();
            if (dataObject != null) {
                System.out.println(dataObject.getDataObjectDefinitionKey());
                System.out.println(dataObject.getDescription());
                System.out.println(dataObject.getExecutionId());
                System.out.println(dataObject.getName());
                System.out.println(dataObject.getValue());
                System.out.println(dataObject.getType());
            }
        }
    }

    /**
     * 删除流程实例（只会删除正在执行相关表数据，不会删除历史表数据）。
     */
    @Test
    public void deleteProcessInstance() {
        String processInstanceId = "72001";
        String deleteReason = "Jacky删除";
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }

    /**
     * 级联删除流程实例（会删除正在执行表和历史表数据）。
     */
    @Test
    public void deleteProcessInstanceCascade() {
        String processInstanceId = "69501";
        String deleteReason = "Jacky删除";
        // ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        //processEngineConfiguration.getExecutionEntityManager().deleteProcessInstance(processInstanceId,deleteReason,true);

        //仿照org.flowable.engine.impl.cmd.DeleteProcessInstanceCmd类，实现级联删除命令类
        managementService.executeCommand(new DeleteProcessInstanceCascadeCmd(processInstanceId, deleteReason));
    }

    /**
     * 获取运行的活动节点。
     * SQL:select * from ACT_RU_EXECUTION where ID_ = ?
     * SQL:select * from ACT_RU_EXECUTION where PARENT_ID_ = ?
     */
    @Test
    public void getActiveActivityIds() {
        String executionId = "45001";//可以是流程实例ID或者执行实例ID
        executionId = "45002";//执行实例ID
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        for (String s : activeActivityIds) {
            System.out.println(s);
        }
    }

    /**
     * Classpath资源部署方式。
     */
    @Test
    public void deployByClasspath2() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .category("流程实例学习分类")
                .name("部署名称-带变量的流程")
                .key("variableProcess")
                .addClasspathResource("dataobject.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 通过流程定义ID启动流程实例。
     */
    @Test
    public void startProcessInstanceById() {
        String processDefinitionId = "dataobject:1:50004";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        System.out.println(processInstance.getId());
    }

    /**
     * 通过流程定义Key和业务Key启动流程实例。
     */
    @Test
    public void startProcessInstanceByKeyAndBusinessKey() {
        String processDefinitionKey = "dataobject";
        String businessKey = "businessKey123";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 判断流程定义是否被挂起。
     * 流程定义表状态SUSPENSION_STATE_：1表示没有被挂起；2表示已经被挂起。
     */
    @Test
    public void isProcessDefinitionSuspended() {
        String processDefinitionId = "dataobject:1:50004";
        boolean processDefinitionSuspended = repositoryService.isProcessDefinitionSuspended(processDefinitionId);
        System.out.println(processDefinitionSuspended);
    }

    /**
     * 挂起流程定义。
     */
    @Test
    public void suspendProcessDefinitionById() {
        String processDefinitionId = "dataobject:1:50004";
        repositoryService.suspendProcessDefinitionById(processDefinitionId);
        boolean processDefinitionSuspended = repositoryService.isProcessDefinitionSuspended(processDefinitionId);
        System.out.println(processDefinitionSuspended);
    }

    /**
     * 重新激活流程定义。
     */
    @Test
    public void activateProcessDefinitionById() {
        String processDefinitionId = "dataobject:1:50004";
        repositoryService.activateProcessDefinitionById(processDefinitionId);
        boolean processDefinitionSuspended = repositoryService.isProcessDefinitionSuspended(processDefinitionId);
        System.out.println(processDefinitionSuspended);
    }

    /**
     * 挂起流程定义以及实例。
     */
    @Test
    public void suspendProcessDefinitionById2() {
        String processDefinitionId = "dataobject:1:50004";
        repositoryService.suspendProcessDefinitionById(processDefinitionId,true,null);
    }

    /**
     * 激活流程定义以及实例。
     */
    @Test
    public void activateProcessDefinitionById2() {
        String processDefinitionId = "dataobject:1:50004";
        repositoryService.activateProcessDefinitionById(processDefinitionId,true,null);
    }

    /**
     * 挂起流程实例。
     */
    @Test
    public void suspendProcessInstanceByProcInstId() {
        String processInstanceId = "57501";
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    /**
     * 激活流程实例。
     */
    @Test
    public void activateProcessInstanceByProcInstId() {
        String processInstanceId = "57501";
        runtimeService.activateProcessInstanceById(processInstanceId);
    }
}
