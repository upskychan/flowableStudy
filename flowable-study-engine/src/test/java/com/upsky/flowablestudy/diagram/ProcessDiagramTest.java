package com.upsky.flowablestudy.diagram;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.FormType;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.form.DateFormType;
import org.flowable.engine.impl.form.EnumFormType;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 流程图测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ProcessDiagramTest {
    private Logger logger = LoggerFactory.getLogger(ProcessDiagramTest.class);

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
     * 生成常规流程图
     */
    @Test
    public void generateDiagram() throws IOException {
        String procDefId = "formkey2:1:200007";
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        String imageType = "PNG";
        List<String> highLightedActivities = new ArrayList<String>();
        List<String> highLightedFlows = new ArrayList<String>();
        String activityFontName = "宋体";
        String labelFontName = "宋体";
        String annotationFontName = "宋体";
        ClassLoader customClassLoader = null;
        double scaleFactor = 1.0D;
        boolean drawSequenceFlowNameWithNoLabelDI = true;
        ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
        InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI);

        FileUtils.copyInputStreamToFile(inputStream, new File("E:/test/processDiagram.png"));
    }

    /**
     * 生成高亮流程图
     */
    @Test
    public void generateHighLightedActivitiesDiagram() throws IOException {
        String procDefId = "formkey2:1:200007";
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        String imageType = "PNG";
        List<String> highLightedActivities = new ArrayList<String>();
        highLightedActivities.add("sid-03FFE656-10E2-4E8A-A92B-1EA78ED614B4");
        List<String> highLightedFlows = new ArrayList<String>();
        highLightedFlows.add("sid-C31549A5-0283-4E92-BFCE-041C752343A3");
        String activityFontName = "宋体";
        String labelFontName = "宋体";
        String annotationFontName = "宋体";
        ClassLoader customClassLoader = null;
        double scaleFactor = 1.0D;
        boolean drawSequenceFlowNameWithNoLabelDI = true;
        ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
        InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel, imageType, highLightedActivities, highLightedFlows, activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI);

        FileUtils.copyInputStreamToFile(inputStream, new File("E:/test/processDiagram.png"));
    }

    /**
     * 测试自定义图片生成器是否生效
     *
     * @throws IOException
     */
    @Test
    public void getProcessDiagramGenerator() throws IOException {
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        System.out.println(processDiagramGenerator);

    }

    @Test
    public void includeProcessVariables() throws IOException {
        List<Task> taskList = (taskService).createTaskQuery().taskCandidateOrAssigned("zhangsan")
                .includeProcessVariables().or().taskCandidateOrAssigned("lisi").includeProcessVariables()
                .endOr().taskCandidateOrAssigned("wangwu").includeProcessVariables().list();
    }

}

