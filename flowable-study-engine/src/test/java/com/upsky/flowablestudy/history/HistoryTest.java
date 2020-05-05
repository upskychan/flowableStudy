package com.upsky.flowablestudy.history;

import com.upsky.flowablestudy.variable.Person;
import org.flowable.common.engine.api.history.HistoricData;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.ProcessInstanceHistoryLog;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史数据测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class HistoryTest {
    private Logger logger = LoggerFactory.getLogger(HistoryTest.class);

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
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("历史数据查询相关的测试").name("historyTest").key("dKey-variable").addClasspathResource("bpmn_xml/历史数据查询.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
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
        String procInstId = "167501";
    }

    /**
     * 查询已完成流程实例
     * SQL:select distinct RES.* , DEF.KEY_ as PROC_DEF_KEY_, DEF.NAME_ as PROC_DEF_NAME_, DEF.VERSION_ as PROC_DEF_VERSION_, DEF.DEPLOYMENT_ID_ as DEPLOYMENT_ID_
     * from ACT_HI_PROCINST RES left outer join ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_ WHERE RES.END_TIME_ is not NULL order by RES.ID_ asc
     */
    @Test
    public void testQueryHistoricProcessInstance() {
        List<HistoricProcessInstance> hisProcInstList = historyService.createHistoricProcessInstanceQuery().finished().list();
        if (hisProcInstList != null) {
            for (HistoricProcessInstance hisProcInst : hisProcInstList) {
                logger.info(hisProcInst.getId() + ":" + hisProcInst.getName() + ":" + hisProcInst.getBusinessKey() + ":" + hisProcInst.getProcessDefinitionKey() + ":" + hisProcInst.getDurationInMillis());
            }
        }
    }

    /**
     * 查询历史流程活动
     * SQL:select RES.* from ACT_HI_ACTINST RES WHERE RES.END_TIME_ is not null order by RES.ID_ asc
     */
    @Test
    public void testHistoricActivityInstanceQuery() {
        List<HistoricActivityInstance> hisActInstList = historyService.createHistoricActivityInstanceQuery().finished().list();
        if (hisActInstList != null) {
            String lastProcInstId = null;
            for (HistoricActivityInstance hisActInst : hisActInstList) {
                String procInstId = hisActInst.getProcessInstanceId();
                if (!procInstId.equals(lastProcInstId)) {
                    lastProcInstId = procInstId;
                    logger.info("****************流程实例{}**************", hisActInst.getProcessInstanceId());
                }
                logger.info("活动节点ID：{}-名称：{}-类型：{}-操作人：{}", hisActInst.getActivityId(), hisActInst.getActivityName(), hisActInst.getActivityType(), hisActInst.getAssignee());
            }
        }
    }

    /**
     * 查询某流程实例一共经历了多少个任务
     * SQL:select distinct RES.* from ACT_HI_TASKINST RES WHERE RES.PROC_INST_ID_ = ? order by RES.START_TIME_ asc
     */
    @Test
    public void testHistoricTaskInstanceQuery() {
        String procInstId = "35001";
        List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).orderByHistoricTaskInstanceStartTime().asc().list();
        if (hisTaskList != null) {
            for (HistoricTaskInstance hisTask : hisTaskList) {
                logger.info("{}-{}-{}-{}ms", hisTask.getId(), hisTask.getName(), hisTask.getAssignee(), hisTask.getDurationInMillis());
            }
        }
    }

    /**
     * 查询历史变量表
     * SQL:select RES.* from ACT_HI_VARINST RES WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     */
    @Test
    public void testHistoricVariableInstanceQuery() {
        String procInstId = "135001";
        List<HistoricVariableInstance> hisVarInstList = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).list();
        if (hisVarInstList != null) {
            for (HistoricVariableInstance hisVar : hisVarInstList) {
                logger.info("{}-{}-{}", hisVar.getVariableName(), hisVar.getVariableTypeName(), hisVar.getValue());
            }
        }
    }

    /**
     * 痕迹日志查询
     * select * from ACT_HI_PROCINST where PROC_INST_ID_ = ?
     */
    @Test
    public void testHistoryLogQuery() {
        String processInstanceId="135001";
        ProcessInstanceHistoryLog processInstanceHistoryLog = historyService
                .createProcessInstanceHistoryLogQuery(processInstanceId)
                .includeTasks()
                .includeActivities()
                .singleResult();
        List<HistoricData> historicData = processInstanceHistoryLog.getHistoricData();
        for (HistoricData hisData :historicData){
            if (hisData instanceof HistoricTaskInstanceEntity ){
                HistoricTaskInstanceEntity historicTaskInstanceEntity= (HistoricTaskInstanceEntity) hisData;
                logger.info("任务-{}:{}",historicTaskInstanceEntity.getName(),historicTaskInstanceEntity.getAssignee());
            }
            if (hisData instanceof  HistoricActivityInstance){
                HistoricActivityInstance hai= (HistoricActivityInstance) hisData;
                logger.info("活动节点{}:{}",hai.getActivityId(),hai.getActivityName());
            }
        }
        logger.info("{}-{}-{}",processInstanceHistoryLog.getId(),processInstanceHistoryLog.getBusinessKey(),processInstanceHistoryLog.getEndTime());
    }

    /**
     * 历史权限-任务
     * select * from ACT_HI_TASKINST where ID_ = ?
     * select * from ACT_HI_IDENTITYLINK where TASK_ID_ = ?
     */
    @Test
    public void getHistoricIdentityLinksForTask() {
        String taskId = "40003";
        List<HistoricIdentityLink> hisIdentityLinks = historyService.getHistoricIdentityLinksForTask(taskId);
        if (hisIdentityLinks != null) {
            for (HistoricIdentityLink hisIdentityLink : hisIdentityLinks) {
                logger.info(hisIdentityLink.getUserId());
            }
        }
    }

    /**
     * 历史权限-流程实例
     * select * from ACT_HI_IDENTITYLINK where PROC_INST_ID_ = ?
     */
    @Test
    public void getHistoricIdentityLinksForProcessInstance() {
        String procInstId = "35001";
        List<HistoricIdentityLink> hisIdentityLinks = historyService.getHistoricIdentityLinksForProcessInstance(procInstId);
        if (hisIdentityLinks != null) {
            for (HistoricIdentityLink hisIdentityLink : hisIdentityLinks) {
                logger.info(hisIdentityLink.getUserId());
            }
        }
    }

}

