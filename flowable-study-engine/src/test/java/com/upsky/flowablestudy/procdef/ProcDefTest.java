package com.upsky.flowablestudy.procdef;

import org.apache.commons.io.FileUtils;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 流程定义测试。
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:flowable-context.xml")
public class ProcDefTest {
    ProcessEngine processEngine;
    RepositoryService repositoryService;

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
    }

    /**
     * DeploymentBuilder构造方法。
     */
    @Test
    public void testDeploymentBuild() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("名称");
        System.out.println(deploymentBuilder);
    }

    /**
     * Classpath资源部署方式。
     */
    @Test
    public void deployByClasspath() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").addClasspathResource("员工请假流程模型.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 字符串方式部署（默认是按照UTF-8编码处理）。
     * 资源的名称resourceName必须是String[] { "bpmn20.xml", "bpmn" } 结尾的才可以部署到流程定义表。
     */
    @Test
    public void deployByString() {
        String text = IoUtil.readFileAsString("员工请假流程模型.bpmn20.xml");
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").key("测试的key").addString("员工请假.bpmn", text);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 输入流方式部署。
     */
    @Test
    public void deployByInputStream() {
        String filePath = "员工请假流程模型.bpmn20.xml";
        InputStream resourceStream = ProcDefTest.class.getClassLoader().getResourceAsStream(filePath);
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").key("测试的key").addInputStream("员工请假.bpmn", resourceStream);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 压缩流方式部署（一次部署，可生成多个流程定义）。
     */
    @Test
    public void deployByZipInputStream() {
        String filePath = "bpmn.zip";
        InputStream resourceStream = ProcDefTest.class.getClassLoader().getResourceAsStream(filePath);
        ZipInputStream zipInputStream = new ZipInputStream(resourceStream);
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").key("测试的key").addZipInputStream(zipInputStream);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 字节方式部署。
     */
    @Test
    public void deployByBytes() {
        String filePath = "员工请假流程模型.bpmn20.xml";
        String inputStreamName = "员工请假流程模型";
        String resourceName = "员工请假流程模型.bpmn";
        InputStream resourceStream = ProcDefTest.class.getClassLoader().getResourceAsStream(filePath);
        byte[] bytes = IoUtil.readInputStream(resourceStream, inputStreamName);
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category("测试分类").name("部署名称").key("测试的key").addBytes(resourceName, bytes);
        Deployment deploy = deploymentBuilder.deploy();
        System.out.println(deploy.getId());
    }

    /**
     * 查询流程定义（ACT_RE_PROCDEF表）。
     */
    @Test
    public void testQueryProcessDef() {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().processDefinitionNameLike("%请假%").processDefinitionKey("holiday").orderByDeploymentId().asc().list();
//        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().processDefinitionKey("holiday").latestVersion().orderByDeploymentId().asc().list();
        if (processDefinitionList != null) {
            System.out.println("一共查询到" + processDefinitionList.size() + "条流程定义记录：");
            for (ProcessDefinition processDefinition : processDefinitionList) {
                System.out.println(processDefinition.getDeploymentId() + ":" + processDefinition.getResourceName());
            }
        }
    }

    /**
     * 删除部署的流程定义。
     */
    @Test
    public void testDeleteDeployment() {
        String deploymentId = "2501";//act_re_deployment表对应的ID
        try {
            //如果当前流程定义下有正在执行的流程，则抛异常。
            repositoryService.deleteDeployment(deploymentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 级联删除部署的流程定义（级联删除会删除当前流程定义下面所有的流程实例）。
     */
    @Test
    public void testDeleteDeploymentCaseCade() {
        String deploymentId = "2501";//act_re_deployment表对应的ID
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 查看流程部署的资源文件。
     *
     * @throws IOException
     */
    @Test
    public void testViewImage() throws IOException {
        String deploymentId = "5001";//act_re_deployment表对应的ID
        List<String> deploymentResourceNames = repositoryService.getDeploymentResourceNames(deploymentId);
        if (deploymentResourceNames != null) {
            for (String deploymentResourceName : deploymentResourceNames) {
                if (deploymentResourceName.endsWith(".png")) {
                    File imgFile = new File(deploymentResourceName);
                    InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, deploymentResourceName);
                    FileUtils.copyInputStreamToFile(resourceAsStream, imgFile);
                }
            }
        }
    }

    /**
     * 流程部署表查询。
     */
    @Test
    public void testQueryDeployment() {
        List<Deployment> deploymentList = repositoryService.createDeploymentQuery().deploymentKeyLike("测试%").list();
        if (deploymentList != null) {
            System.out.println("一共查询到" + deploymentList.size() + "条流程部署记录：");
            for (Deployment deployment : deploymentList) {
                System.out.println(deployment.getId() + ":" + deployment.getKey() + ":" + deployment.getName());
            }
        }
    }

    /**
     * 本地SQL查询流程部署。
     */
    @Test
    public void testNativeDeploymentQuery() {
        String sql = "SELECT * FROM act_re_deployment ORDER BY DEPLOY_TIME_ DESC";
        List<Deployment> deploymentList = repositoryService.createNativeDeploymentQuery().sql(sql).list();
        if (deploymentList != null) {
            System.out.println("一共查询到" + deploymentList.size() + "条流程部署记录：");
            for (Deployment deployment : deploymentList) {
                System.out.println(deployment.getId() + ":" + deployment.getKey() + ":" + deployment.getName());
            }
        }
    }

}
