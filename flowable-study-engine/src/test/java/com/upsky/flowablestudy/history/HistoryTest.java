package com.upsky.flowablestudy.history;

import com.upsky.flowablestudy.variable.Person;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
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
                logger.info("活动节点ID：{}", hisActInst.getActivityId());
                logger.info("活动节点名称：{}", hisActInst.getActivityName());
                logger.info("活动节点类型：{}", hisActInst.getActivityType());
                logger.info("操作人：{}", hisActInst.getAssignee());
            }
        }
    }

}

