package com.upsky.flowablestudy.variable;

import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程变量测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class VariableTest {
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
     * 部署
     */
    @Test
    public void deploy() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("流程变量测试").name("variableTest").key("dKey-variable").addClasspathResource("bpmn_xml/变量测试流程.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "variable";
        String businessKey = "bKey-variable";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        System.out.println(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 指定任务ID设置变量。
     * 更新表：insert into ACT_RU_VARIABLE;insert into ACT_HI_VARINST
     */
    @Test
    public void setVariablesByTaskId() {
        String procInstId = "135001";
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        System.out.println(task.getId() + ":" + task.getName());
        //单个设置变量
        taskService.setVariable(task.getId(), "请假人", "张三");
        taskService.setVariable(task.getId(), "请假天数", "3");
        //设置本地变量（任务私有）
        taskService.setVariableLocal(task.getId(), "currentNodeName", task.getName());
        //批量设置变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("请假日期", new Date());
        taskService.setVariables(task.getId(), variables);
    }

    /**
     * 完成任务时设置变量。
     */
    @Test
    public void completeTask() {
        String procInstId = "135001";
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        Map<String, Object> variables = new HashMap<>();
        variables.put("完成人", "张三");
        Map<String, Object> transientVariables = new HashMap<>();
        transientVariables.put("完成人_transient", "张三_transient");
        taskService.complete(task.getId(), variables, transientVariables);
    }

    /**
     * 设置JavaBean类型的变量。
     */
    @Test
    public void setVariablesByTaskIdBean() {
        String procInstId = "135001";
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        Map<String, Object> variables = new HashMap<>();
        Person person = new Person();
        person.setName("李四");
        person.setAge(18);
        variables.put("待操作人", person);
        taskService.setVariables(task.getId(), variables);
    }

    /**
     * 获取流程实例的变量。
     */
    @Test
    public void getVariables() {
        String procInstId = "135001";
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        //获取流程实例的所有变量
        Map<String, Object> variables = taskService.getVariables(task.getId());
        System.out.println(variables);
        //获取流程实例的指定变量
        String varName = "请假日期";
        Object holidayDate = taskService.getVariable(task.getId(), varName);
        System.out.println("holidayDate:" + holidayDate);
        varName = "待操作人";
        Object todoPerson = taskService.getVariable(task.getId(), varName);
        if (todoPerson != null && todoPerson instanceof Person) {
            Person person = (Person) todoPerson;
            System.out.println("待操作人：" + person.getName() + "，年龄" + person.getAge());
        }
    }

    /**
     * 获取流程本地变量。
     */
    @Test
    public void getVariablesLocal() {
        String procInstId = "135001";
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        System.out.println(task.getId() + ":" + task.getName());
        //设置本地变量（任务私有）
        taskService.setVariableLocal(task.getId(), "currentNodeName2", task.getName());
        //获取本地变量
        Map<String, Object> variablesLocal = taskService.getVariablesLocal(task.getId());
        System.out.println("variablesLocal:" + variablesLocal);
    }

    /**
     * 手工设置流程变量。
     */
    @Test
    public void setVariablesByRuntimeService() {
        //全局变量
        String executionId1 = "135001";//流程实例ID
        Map<String, Object> variables = new HashMap<>();
        variables.put("全局变量_runtime", "我是全局变量");
        runtimeService.setVariables(executionId1, variables);
        //本地变量
        String executionId2 = "135002";//执行实例ID
        Map<String, Object> variablesLocal = new HashMap<>();
        variablesLocal.put("本地变量_runtime", "我是本地变量");
        runtimeService.setVariablesLocal(executionId2, variablesLocal);
        //SQL:select * from ACT_RU_VARIABLE where EXECUTION_ID_ = ? and TASK_ID_ is null
        //获取executionId1的变量
        Map<String, Object> variables1 = runtimeService.getVariables(executionId1);
        System.out.println("variables1:" + variables1);
        Map<String, Object> variables1Local = runtimeService.getVariablesLocal(executionId1);
        System.out.println("variables1Local:" + variables1Local);//得到的结果同getVariables()方法
        //获取executionId2的变量
        Map<String, Object> variables2 = runtimeService.getVariables(executionId2);
        System.out.println("variables2:" + variables2);//会查询出executionId1和executionId2所有变量（包括本地变量），
        Map<String, Object> variables2Local = runtimeService.getVariablesLocal(executionId2);
        System.out.println("variables2Local:" + variables2Local);//查询出executionId2的本地变量
    }

    /**
     * 查询历史变量。
     * SQL: select RES.* from ACT_HI_VARINST RES WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     */
    @Test
    public void testHistoricVariableInstanceQuery() {
        String procInstId = "135001";
        List<HistoricVariableInstance> varList = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).list();
        if (varList != null) {
            System.out.println("流程实例" + procInstId + "的变量共有" + varList.size() + "个：");
            for (HistoricVariableInstance hisVarInst : varList) {
                System.out.println(hisVarInst.getVariableName() + ":" + hisVarInst.getVariableTypeName() + ":" + hisVarInst.getValue());
            }
        }
    }

    /**
     * 启动流程时设置临时变量（只有下面方式能在启动流程时设置临时变量）。
     */
    @Test
    public void testProcessInstanceBuilder() {
        String processDefinitionKey = "variable";
        Map<String, Object> transientVariables = new HashMap<String, Object>();
        transientVariables.put("a", "a");
        transientVariables.put("b", "b");
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        ProcessInstance procInst = processInstanceBuilder.processDefinitionKey(processDefinitionKey).transientVariables(transientVariables).start();
        System.out.println(procInst.getProcessInstanceId());
    }

    /**
     * 完成任务时设置临时变量。
     */
    @Test
    public void completeTask2() {
        String procInstId = "157501";
        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        Map<String, Object> transientVariables = new HashMap<>();
        transientVariables.put("完成人_transient", "张三_transient");
        taskService.complete(task.getId(), null, transientVariables);
    }

}

