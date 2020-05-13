package com.upsky.flowablestudy.form;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.FormType;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.form.DateFormType;
import org.flowable.engine.impl.form.EnumFormType;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class FormTest {
    private Logger logger = LoggerFactory.getLogger(FormTest.class);

    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private IdentityService identityService;
    private ManagementService managementService;
    private DynamicBpmnService dynamicBpmnService;
    private FormService formService;

    @Before
    public void buildProcessEngine() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
        repositoryService = processEngine.getRepositoryService();
        System.out.println(repositoryService);
        String name = processEngine.getName();
        System.out.println("流程引擎的名称：" + name);
        dynamicBpmnService = processEngine.getDynamicBpmnService();
        System.out.println(dynamicBpmnService);
        formService = processEngine.getFormService();
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
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("表单相关的测试").name("动态表单").key("dKey-form").addClasspathResource("bpmn_xml/form.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        logger.info("deloyId:{}", deploy.getId());
    }

    /**
     * 获取流程定义启动节点的表单
     */
    @Test
    public void getStartFormData() {
        String procDefId = "form:1:185004";
        StartFormData startFormData = formService.getStartFormData(procDefId);
        logger.info("getProcessDefinition:{}", startFormData.getProcessDefinition().toString());
        logger.info("getDeploymentId:{}", startFormData.getDeploymentId());
        logger.info("getFormKey:{}", startFormData.getFormKey());
        List<FormProperty> formPropertyList = startFormData.getFormProperties();
        for (FormProperty formProperty : formPropertyList) {
            logger.info("formProperty.getId():{}", formProperty.getId());
            logger.info("formProperty.getName():{}", formProperty.getName());
            FormType formType = formProperty.getType();
            logger.info("formProperty.getType():{}", formType);
            String key = "";
            if (formType instanceof EnumFormType) {
                key = "values";
            } else if (formType instanceof DateFormType) {
                key = "datePattern";
            }
            Object formTypeInformation = formType.getInformation(key);
            logger.info("formTypeInformation:{}", formTypeInformation);
            logger.info("formProperty.getValue():{}", formProperty.getValue());
            System.out.println("******************************************");
        }
    }

    /**
     * 提交表单启动流程实例
     */
    @Test
    public void startProcessInstanceBySubmitForm() {
        String procDefId = "form:1:185004";
        String businessKey = "bKey-submitForm";
        Map<String, String> vars = new HashMap<String, String>();
        Date now = new Date();
        vars.put("start_date", getDate(now));
        vars.put("end_date", getDate(new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000L)));
        vars.put("reason", "我想出去玩玩");
        vars.put("days", "3");
        ProcessInstance processInstance = formService.submitStartFormData(procDefId, businessKey, vars);
        logger.info(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 格式化日期
     *
     * @param date date
     * @return 日期
     */
    private String getDate(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 获取任务的表单
     */
    @Test
    public void getTaskFormData() {
        String taskId = "190014";
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        logger.info("getDeploymentId:{}", taskFormData.getDeploymentId());
        logger.info("getFormKey:{}", taskFormData.getFormKey());
        List<FormProperty> formPropertyList = taskFormData.getFormProperties();
        for (FormProperty formProperty : formPropertyList) {
            logger.info("formProperty.getId():{}", formProperty.getId());
            logger.info("formProperty.getName():{}", formProperty.getName());
            FormType formType = formProperty.getType();
            logger.info("formProperty.getType():{}", formType);
            String key = "";
            if (formType instanceof EnumFormType) {
                key = "values";
            } else if (formType instanceof DateFormType) {
                key = "datePattern";
            }
            Object formTypeInformation = formType.getInformation(key);
            logger.info("formTypeInformation:{}", formTypeInformation);
            logger.info("formProperty.getValue():{}", formProperty.getValue());
            System.out.println("******************************************");
        }
    }

    /**
     * 试一下直接使用获取任务的变量能否取到动态表单（可以）
     */
    @Test
    public void testGetVariablesByTaskId() {
        String taskId = "190014";
        Map<String, Object> variables = taskService.getVariables(taskId);
        logger.info("variables:{}", variables);
    }

    /**
     * 保存任务的动态表单数据
     */
    @Test
    public void saveFormData() {
        String taskId = "190014";
        //required="true"的字段必须设置
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("addMore", "我再添加一个表单变量");
        vars.put("reason", "我又想出去玩玩");
        vars.put("days", "4");
        formService.saveFormData(taskId, vars);
    }

    /**
     * 保存任务的动态表单数据并完成任务
     */
    @Test
    public void submitTaskFormData() {
        String taskId = "190014";
        //required="true"的字段必须设置
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("addMore", "我再添加一个表单变量");
        vars.put("reason", "我叒想出去玩玩");
        vars.put("days", "5");
        formService.submitTaskFormData(taskId, vars);
    }

    /**
     * 外置表单部署
     */
    @Test
    public void deploy2() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder = deploymentBuilder.category("表单相关的测试").name("外置表单").key("dKey-renderform");
        deploymentBuilder = deploymentBuilder.addClasspathResource("bpmn_xml/formkey2.bpmn20.xml").addClasspathResource("start.html").addClasspathResource("task.html").addClasspathResource("task2.html");
        Deployment deployment = deploymentBuilder.deploy();
        logger.info("deloyId:{}", deployment.getId());
    }

    /**
     * 获取开始节点的外置表单key
     */
    @Test
    public void getStartFormKey() {
        String procDefId = "formkey2:1:200007";
        String startFormKey = formService.getStartFormKey(procDefId);
        logger.info("开始节点定义的表单key:{}", startFormKey);
    }

    /**
     * 获取开始节点的外置表单
     */
    @Test
    public void getRenderedStartForm() {
        String procDefId = "formkey2:1:200007";
        //方法一
        Object renderedStartForm = formService.getRenderedStartForm(procDefId);
        logger.info("开始节点定义的外置表单资源:{}" + renderedStartForm);
        //方法二
        Object renderedStartForm2 = formService.getRenderedStartForm(procDefId, null);
        logger.info("开始节点定义的外置表单资源2:{}" + renderedStartForm2);
        //方法三
        Object renderedStartForm3 = formService.getRenderedStartForm(procDefId, "juel");
        logger.info("开始节点定义的外置表单资源3:{}" + renderedStartForm3);
    }

    /**
     * 格式化日期
     *
     * @param date date
     * @return 日期
     */
    private String getDate2(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 提交表单并启动流程实例
     */
    @Test
    public void submitStartFormData2() {
        String procDefId = "formkey2:1:200007";
        Map<String, String> vars = new HashMap<String, String>();
        Date now = new Date();
        vars.put("startDate", getDate2(now));
        vars.put("endDate", getDate2(new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000L)));
        vars.put("reason", "我想出去玩玩");
        ProcessInstance processInstance = formService.submitStartFormData(procDefId, vars);
        logger.info("流程实例ID:{}", processInstance.getProcessInstanceId());
    }

    /**
     * 获取任务节点外置表单并渲染
     */
    @Test
    public void getRenderedTaskForm() {
        String taskId = "205006";
        //方法一
        Object renderedTaskForm = formService.getRenderedTaskForm(taskId);
        logger.info("renderedTaskForm1:{}", renderedTaskForm);
        //方法二
        Object renderedTaskForm2 = formService.getRenderedTaskForm(taskId, null);
        logger.info("renderedTaskForm2:{}", renderedTaskForm2);
        //方法三
        Object renderedTaskForm3 = formService.getRenderedTaskForm(taskId, "juel");
        logger.info("renderedTaskForm3:{}", renderedTaskForm3);
    }

    /**
     * 提交任务表单并完成任务
     */
    @Test
    public void submitTaskFormData2() {
        String taskId = "202512";
        Map<String, String> vars = new HashMap<String, String>();
        Date now = new Date();
        vars.put("startDate", getDate2(now));
        vars.put("endDate", getDate2(new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000L)));
        vars.put("reason", "我又想出去玩玩");
        formService.submitTaskFormData(taskId, vars);
    }

    /**
     * 获取运行任务的外置表单key
     */
    @Test
    public void getTaskFormData2() {
        String taskId = "205006";
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        String formKey = taskFormData.getFormKey();
        logger.info("formKey:{}", formKey);
    }

    /**
     * 获取任意节点（运行和非运行任务）的外置表单key
     */
    @Test
    public void getFormKey() {
        String procDefId = "formkey2:1:200007";
        String startFormKey = formService.getStartFormKey(procDefId);
        logger.info("startFormKey:{}", startFormKey);
        String nodeId = "sid-4CD86265-1E97-4CC7-99BD-689F65C4C297";
        String taskFormKey = formService.getTaskFormKey(procDefId, nodeId);
        logger.info("taskFormKey:{}", taskFormKey);
    }

    /**
     * 自定义工具类获取开始节点外置表单key
     */
    @Test
    public void getStartEventFormKey() {
        String procDefId = "formkey2:1:200007";
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        Process mainProcess = bpmnModel.getMainProcess();
        List<StartEvent> flowElementsOfType = mainProcess.findFlowElementsOfType(StartEvent.class);
        for (StartEvent startEvent : flowElementsOfType) {
            String formKey = startEvent.getFormKey();
            logger.info("开始节点的ID：" + startEvent.getId() + ",formkey:" + formKey);
        }
    }

    /**
     * 自定义工具类获取任务节点外置表单key
     */
    @Test
    public void getUserTaskFormKey() {
        String procDefId = "formkey2:1:200007";
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        Process mainProcess = bpmnModel.getMainProcess();
        List<UserTask> flowElementsOfType = mainProcess.findFlowElementsOfType(UserTask.class);
        for (UserTask userTask : flowElementsOfType) {
            String formKey = userTask.getFormKey();
            logger.info("任务的ID：" + userTask.getId() + ",formkey:" + formKey);
        }
    }
}

