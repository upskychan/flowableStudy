package com.upsky.flowablestudy.service.impl;

import com.upsky.flowablestudy.service.IFlowService;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Primary
@Service
public class FlowServiceImpl implements IFlowService {
    private static final Logger log = LoggerFactory.getLogger(FlowServiceImpl.class);

    /**
     * Flowable运行时服务
     */
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Deployment createFlow(String filePath) {
        //解析BPMN模型看是否成功
        XMLStreamReader reader = null;
        InputStream inputStream = null;
        try {
            BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            inputStream = new FileInputStream(new File(filePath));
            reader = factory.createXMLStreamReader(inputStream);
            BpmnModel model = bpmnXMLConverter.convertToBpmnModel(reader);
            List<Process> processes = model.getProcesses();
            Process curProcess = null;
            if (CollectionUtils.isEmpty(processes)) {
                log.error("BPMN模型没有配置流程");
                return null;
            }

            curProcess = processes.get(0);

            inputStream = new FileInputStream(new File(filePath));
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("TEST_FLOW").addInputStream(curProcess.getName(), inputStream);

            Deployment deployment = deploymentBuilder.deploy();

            log.warn("部署流程 name:" + curProcess.getName() + " " + deployment);
            return deployment;
        } catch (Exception e) {
            log.error("BPMN模型创建流程异常", e);
            return null;
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                log.error("关闭异常", e);
            }
        }
    }
}
