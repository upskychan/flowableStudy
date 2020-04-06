package com.upsky.flowablestudy.procinst;

import org.flowable.engine.impl.util.ProcessInstanceHelper;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * 扩展流程实例帮助类.
 */
public class JackyProcessInstanceHelper extends ProcessInstanceHelper {
    @Override
    public ProcessInstance createProcessInstance(ProcessDefinition processDefinition, String businessKey, String processInstanceName, Map<String, Object> variables, Map<String, Object> transientVariables) {
        System.out.println("JackyProcessInstanceHelper.createProcessInstance()");
        return super.createProcessInstance(processDefinition, businessKey, processInstanceName, variables, transientVariables);
    }

    @Override
    public ProcessInstance createProcessInstance(ProcessDefinition processDefinition, String businessKey, String processInstanceName, String overrideDefinitionTenantId, String predefinedProcessInstanceId, Map<String, Object> variables, Map<String, Object> transientVariables, String callbackId, String callbackType, String referenceId, String referenceType, String stageInstanceId, boolean startProcessInstance) {
        System.out.println("JackyProcessInstanceHelper.createProcessInstance()");
        return super.createProcessInstance(processDefinition, businessKey, processInstanceName, overrideDefinitionTenantId, predefinedProcessInstanceId, variables, transientVariables, callbackId, callbackType, referenceId, referenceType, stageInstanceId, startProcessInstance);
    }
}
