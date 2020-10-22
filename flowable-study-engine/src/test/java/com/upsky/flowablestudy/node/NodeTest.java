package com.upsky.flowablestudy.node;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 部署并启动流程。
     */
    @Test
    public void testDeploymentAndStartProcessInstance() {
        String filePath = "个人任务测试.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-个人任务测试")
//                .key("receivetask")
                .addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "singletask";
        String businessKey = "businessKey-singletask";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 查询指定人的待办任务。
     * select distinct RES.* from ACT_RU_TASK RES WHERE RES.ASSIGNEE_ = ? order by RES.ID_ asc
     */
    @Test
    public void findMyTask() {
        String assignee = "张三丰";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getCreateTime());
        }
    }

    /**
     * 完成待办任务。
     */
    @Test
    public void completeTask() {
        String taskId = "70010";
        taskService.complete(taskId);
    }

    /**
     * 部署并启动流程。
     */
    @Test
    public void testDeploymentAndStartProcessInstance2() {
        String filePath = "个人任务测试2.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-个人任务测试")
//                .key("receivetask")
                .addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "singletask";
        String businessKey = "businessKey-singletask";
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("userId", "陈杰");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        System.out.println(processInstance.getId());
    }

    /**
     * 部署并启动流程。
     */
    @Test
    public void testDeploymentAndStartProcessInstance3() {
        String filePath = "个人任务测试3.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-个人任务测试")
//                .key("receivetask")
                .addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "singletask";
        String businessKey = "businessKey-singletask";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 重新设置任务的办理人（认领任务）。
     */
    @Test
    public void testSetAssignee() {
        String assignee = "Jacky";
        String taskId = "";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        for (Task task : tasks) {
            taskId = task.getId();
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
        }
        String userId = "chenjie";
        taskService.setAssignee(taskId, userId);
    }

    /**
     * 部署并启动流程。
     */
    @Test
    public void testDeploymentAndStartProcessInstance4() {
        String filePath = "组任务测试.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-组任务测试")
                .addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "grouptask";
        String businessKey = "businessKey-grouptask";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 查询多个候选人的个人任务（查不到，因为ASSIGNEE_ 为空）。
     * select distinct RES.* from ACT_RU_TASK RES WHERE RES.ASSIGNEE_ = ? order by RES.ID_ asc
     */
    @Test
    public void findMyTaskOfGroupTask() {
        String assignee = "分享牛1";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getCreateTime());
        }
    }

    /**
     * 查询候选人任务（正确方式）。
     * select distinct RES.* from ACT_RU_TASK RES WHERE RES.ASSIGNEE_ is null and exists(select LINK.ID_ from ACT_RU_IDENTITYLINK LINK where LINK.TYPE_ = 'candidate' and LINK.TASK_ID_ = RES.ID_ and ( LINK.USER_ID_ = ? ) ) order by RES.ID_ asc
     */
    @Test
    public void findGroupTask() {
        String userId = "分享牛1";
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getCreateTime());
        }
    }

    /**
     * 查询候选人任务的处理人。
     * select * from ACT_RU_IDENTITYLINK where TASK_ID_ = ?
     */
    @Test
    public void findGroupTaskUserList() {
        String taskId="85010";
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(taskId);
        for (IdentityLink identityLink : identityLinksForTask){
            System.out.println("#####################");
            System.out.println(identityLink.getProcessDefinitionId());
            System.out.println(identityLink.getGroupId());
            System.out.println(identityLink.getUserId());
            System.out.println(identityLink.getTaskId());
        }
    }

    /**
     * 查询历史任务的处理人。
     * select * from ACT_HI_IDENTITYLINK where TASK_ID_ = ?
     */
    @Test
    public void findHiGroupTaskUserList() {
        String taskId="85010";
        List<HistoricIdentityLink> historicIdentityLinksForTask = historyService.getHistoricIdentityLinksForTask(taskId);
        for (HistoricIdentityLink identityLink : historicIdentityLinksForTask){
            System.out.println("#####################");
            System.out.println(identityLink.getGroupId());
            System.out.println(identityLink.getUserId());
            System.out.println(identityLink.getTaskId());
        }
    }

    /**
     * 认领任务并完成任务。
     */
    @Test
    public void claimGroupTask() {
        String taskId="85010";
        String userId="分享牛2";
        taskService.claim(taskId,userId);

        taskService.complete(taskId);
    }

    /**
     * 部署并启动流程。
     */
    @Test
    public void testDeploymentAndStartProcessInstance5() {
        String filePath = "bpmn_xml/组任务测试2.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-组任务测试2").addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "grouptask";
        String businessKey = "businessKey-grouptask2-2";
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("userIds", "分享牛4,分享牛5,分享牛6");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        System.out.println(processInstance.getId());
    }

    /**
     * 使用其他用户认领任务。
     */
    @Test
    public void claimGroupTask2() {
        String taskId = "232511";
        String userId = "分享牛5-1";
//        taskService.claim(taskId, null);
        taskService.claim(taskId, userId);
    }

    /**
     * 部署并启动流程。
     */
    @Test
    public void testDeploymentAndStartProcessInstance6() {
        String filePath = "组任务测试3.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-组任务测试3").addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "grouptask";
        String businessKey = "businessKey-grouptask3";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 查询组任务并认领，完成任务
     */
    @Test
    public void findGroupTaskAndClaimAndComplete() {
        String userId = "陈杰7";
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
        for (Task task : tasks) {
            String taskId = task.getId();
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getCreateTime());
            //认领任务
            taskService.claim(taskId, userId);
            //完成任务
            taskService.complete(taskId);
        }
    }

    /**
     * 分配个人任务回退到组任务（前提：之前是个组任务）
     */
    @Test
    public void setAssigneeNull() {
        String taskId = "95011";
        taskService.setAssignee(taskId, null);
    }

    /**
     * 组任务增加候选人
     */
    @Test
    public void addCandidateUser() {
        String taskId = "95011";
        String userId = "Jacky10";
        taskService.addCandidateUser(taskId, userId);
    }

    /**
     * 组任务删除候选人
     */
    @Test
    public void deleteCandidateUser() {
        String taskId = "95011";
        String userId = "Jacky10";
        taskService.deleteCandidateUser(taskId, userId);
    }

    /**
     * 部署并启动流程（角色任务）。
     */
    @Test
    public void testDeploymentAndStartProcessInstance7() {
        String filePath = "组任务测试4.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程实例学习分类").name("部署名称-组任务测试4").addClasspathResource(filePath);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());

        String processDefinitionKey = "grouptask";
        String businessKey = "businessKey-grouptask4";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 初始化角色和用户
     */
    @Test
    public void initGroupUsers() {
        GroupEntityImpl groupEntity1 = new GroupEntityImpl();
        groupEntity1.setRevision(0);
        groupEntity1.setName("部门经理");
        groupEntity1.setId("部门经理");

        identityService.saveGroup(groupEntity1);//建立组
        GroupEntityImpl groupEntity2 = new GroupEntityImpl();
        groupEntity2.setRevision(0);
        groupEntity2.setName("总经理");
        groupEntity2.setId("总经理");
        identityService.saveGroup(groupEntity2);//建立组

        UserEntityImpl userEntity1 = new UserEntityImpl();
        userEntity1.setRevision(0);
        userEntity1.setId("张三");
        identityService.saveUser(userEntity1);
        UserEntityImpl userEntity2 = new UserEntityImpl();
        userEntity2.setRevision(0);
        userEntity2.setId("李四");
        identityService.saveUser(userEntity2);
        UserEntityImpl userEntity3 = new UserEntityImpl();
        userEntity3.setRevision(0);
        userEntity3.setId("王五");
        identityService.saveUser(userEntity3);
        identityService.createMembership("张三", "部门经理");//建立组和用户关系
        identityService.createMembership("李四", "部门经理");//建立组和用户关系
        identityService.createMembership("王五", "总经理");//建立组和用户关系
    }

    /**
     * 角色任务查询某个用户的个人任务
     */
    @Test
    public void findGroupTaskByUserId() {
//        String userId = "王五";
        String groupId = "总经理";
        System.out.println(groupId + "角色下的用户：");
        List<User> users = identityService.createUserQuery().memberOfGroup(groupId).list();
        for (User user : users) {
            String userId = user.getId();
            System.out.println(userId);
            List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).list();
            for (Task task : tasks) {
                System.out.println(task.getId());
                System.out.println(task.getName());
                System.out.println(task.getCreateTime());
            }
        }
    }

    /**
     * 直接按角色查询角色任务
     */
    @Test
    public void taskCandidateGroup() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("总经理").list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getCreateTime());
        }
    }

}
