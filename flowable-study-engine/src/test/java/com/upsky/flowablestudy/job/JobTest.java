package com.upsky.flowablestudy.job;

import org.flowable.common.engine.api.history.HistoricData;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.ProcessInstanceHistoryLog;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * 定时任务测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class JobTest {
    private Logger logger = LoggerFactory.getLogger(JobTest.class);

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
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("Job相关的测试").name("JobTest").key("dKey-job").addClasspathResource("bpmn_xml/历史数据查询.bpmn20.xml");
//        Deployment deploy = deploymentBuilder.deploy();
        Date theTime = new Date(new Date().getTime() + 2 * 1000);//2s后执行
        //定时激活流程定义
        Deployment deploy = deploymentBuilder.activateProcessDefinitionsOn(theTime).deploy();
        logger.info("deloyId:{}", deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey() {
        String processDefinitionKey = "history";
        String businessKey = "bKey-history";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        logger.info(processInstance.getId() + "," + processInstance.getActivityId());
    }

    @Test
    public void sleep() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定时挂起流程定义
     */
    @Test
    public void suspendProcessDefinitionById() {
        String procDefId = "history:2:170004";
        Date theTime = new Date(new Date().getTime() + 2 * 1000);//2s后执行
        boolean suspendProcInst = true;
        repositoryService.suspendProcessDefinitionById(procDefId, suspendProcInst, theTime);
    }

    /**
     * 定时激活流程定义
     */
    @Test
    public void activateProcessDefinitionById() {
        String procDefId = "history:2:170004";
        Date theTime = new Date(new Date().getTime() + 2 * 1000);//2s后执行
        boolean suspendProcInst = true;
        repositoryService.activateProcessDefinitionById(procDefId, suspendProcInst, theTime);
    }

    /**
     * 判断流程定义是否flowable5版本
     */
    @Test
    public void isFlowable5ProcessDefinition() {
        String procDefId = "history:2:170004";
        Boolean isFlowable5ProcessDefinition = repositoryService.isFlowable5ProcessDefinition(procDefId);
        logger.info("isFlowable5ProcessDefinition:{}", isFlowable5ProcessDefinition);
    }

    /**
     * 判断流程定义是否被挂起
     */
    @Test
    public void isProcessDefinitionSuspended() {
        String procDefId = "history:2:170004";
        Boolean isProcessDefinitionSuspended = repositoryService.isProcessDefinitionSuspended(procDefId);
        logger.info("isProcessDefinitionSuspended:{}", isProcessDefinitionSuspended);
    }

    /**
     * 定时任务移到任务执行表（act_ru_timer_job至act_ru_job）
     */
    @Test
    public void moveTimerToExecutableJob() {
        String jobId = "175001";
        managementService.moveTimerToExecutableJob(jobId);
    }

    /**
     * 修改重试次数
     * update ACT_RU_JOB SET REV_ = ?, RETRIES_ = ? where ID_= ? and REV_ = ?
     */
    @Test
    public void setJobRetries() {
        String jobId = "175001";
        managementService.setJobRetries(jobId, 11);
    }

    /**
     * 作业执行表移至死信作业表（act_ru_job至act_ru_deadletter_job）
     */
    @Test
    public void moveJobToDeadLetterJob() {
        String jobId = "175001";
        managementService.moveJobToDeadLetterJob(jobId);
    }

    /**
     * 死信作业表移至作业执行表（act_ru_deadletter_job至act_ru_job）
     */
    @Test
    public void moveDeadLetterJobToExecutableJob() {
        String jobId = "175001";
        managementService.moveDeadLetterJobToExecutableJob(jobId, 3);
    }

    /**
     * select * from ACT_RU_JOB where ID_ = ?
     */
    @Test
    public void executeJob() {
        String jobId = "175001";
        managementService.executeJob(jobId);
    }

    /**
     * 部署-异步任务
     */
    @Test
    public void deployAsyncJobProcess() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("Job相关的测试").name("异步任务").key("dKey-asyncJob").addClasspathResource("bpmn_xml/async.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        logger.info("deloyId:{}", deploy.getId());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void startProcessInstanceByKey2() {
        String processDefinitionKey = "async";
        String businessKey = "bKey-asyncJob";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey);
        logger.info(processInstance.getId() + "," + processInstance.getActivityId());
    }

    /**
     * 手工执行作业
     */
    @Test
    public void executeJob2() {
        String jobId = "180005";
        managementService.executeJob(jobId);
    }
}

