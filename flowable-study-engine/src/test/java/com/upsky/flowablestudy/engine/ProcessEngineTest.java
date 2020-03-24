package com.upsky.flowablestudy.engine;

import org.flowable.common.engine.api.engine.EngineLifecycleListener;
import org.flowable.engine.*;
import org.flowable.task.api.Task;
import org.junit.After;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程引擎测试类.
 */
public class ProcessEngineTest {
    ProcessEngine processEngine;

    @Test
    public void testProcessEngine() {
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
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        System.out.println("dynamicBpmnService:" + dynamicBpmnService);
        System.out.println("formService:" + formService);
        System.out.println("historyService:" + historyService);
        System.out.println("identityService:" + identityService);
        System.out.println("managementService:" + managementService);
        System.out.println("repositoryService:" + repositoryService);
        System.out.println("runtimeService:" + runtimeService);
        System.out.println("taskService:" + taskService);

        List<Task> taskList = taskService.createTaskQuery().list();
        if (taskList != null) {
            for (Task task : taskList) {
                System.out.println("TaskName:" + task.getName());
            }
        }
    }

    @Test
    public void testGetBeans() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.print("流程引擎类：" + processEngine);
        System.out.println(",class:" + processEngine.getClass());

        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        System.out.println("ProcessEngineConfiguration:" + processEngineConfiguration);

        //在流程引擎配置类对象中获取Spring容器的bean
        DataSource dataSource = (DataSource) processEngineConfiguration.getBeans().get("dataSource");
        System.out.println("dataSource:" + dataSource);

        System.out.println("notExistBeanName:" + processEngineConfiguration.getBeans().containsKey("notExistBeanName"));
    }

    /**
     * 手动方式创建流程引擎
     */
    @Test
    public void buildEngineByManual() {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
        processEngineConfiguration.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable-idm?useUnicode=true&amp;characterEncoding=UTF-8");
        processEngineConfiguration.setJdbcUsername("root");
        processEngineConfiguration.setJdbcPassword("123456");
        List<EngineLifecycleListener> engineLifecycleListeners = new ArrayList<>();
        engineLifecycleListeners.add(new MyProcessEngineLifecycleListener());
        processEngineConfiguration.setEngineLifecycleListeners(engineLifecycleListeners);

        processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println("buildEngineByManual:" + processEngine);
    }

    /**
     * 其它方式创建流程引擎
     */
    @Test
    public void buildEngineByOther() {
        InputStream inputStream = ProcessEngineTest.class.getClassLoader().getResourceAsStream("flowable.cfg.xml");
        String beanName = "processEngineConfiguration";
//        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(inputStream, beanName);
        String resource = "flowable.cfg.xml";
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(resource);
        processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println("buildEngineByManual:" + processEngine.getName());
    }

    @After
    public void closeProcessEngine() {
        processEngine.close();
    }
}
