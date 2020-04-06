package com.upsky.flowablestudy.procinst;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.Serializable;

/**
 * 自定义级联删除流程实例CMD。
 */
public class DeleteProcessInstanceCascadeCmd implements Command<Void>, Serializable {
    private static final long serialVersionUID = 1L;
    protected String processInstanceId;
    protected String deleteReason;

    public DeleteProcessInstanceCascadeCmd(String processInstanceId, String deleteReason) {
        this.processInstanceId = processInstanceId;
        this.deleteReason = deleteReason;
    }

    /**
     * 实现方式一：模仿org.flowable.engine.impl.cmd.DeleteProcessInstanceCmd类的实现。
     */
    @Override
    public Void execute(CommandContext commandContext) {
        if (processInstanceId == null) {
            throw new FlowableIllegalArgumentException("processInstanceId is null");
        }

        ExecutionEntity processInstanceEntity = CommandContextUtil.getExecutionEntityManager(commandContext).findById(processInstanceId);
        if (processInstanceEntity == null) {
            throw new FlowableObjectNotFoundException("No process instance found for id '" + processInstanceId + "'", ProcessInstance.class);
        }
        if (processInstanceEntity.isDeleted()) {
            return null;
        }

        CommandContextUtil.getExecutionEntityManager(commandContext).deleteProcessInstance(processInstanceEntity.getProcessInstanceId(), deleteReason, true);

        return null;
    }

    /**
     * 实现方式二。
     */
//    @Override
    public Void executeBak(CommandContext commandContext) {
        if (processInstanceId == null) {
            throw new FlowableIllegalArgumentException("processInstanceId is null");
        }
        AbstractEngineConfiguration currentEngineConfiguration = commandContext.getCurrentEngineConfiguration();
        if (currentEngineConfiguration != null && currentEngineConfiguration instanceof ProcessEngineConfigurationImpl) {
            ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) currentEngineConfiguration;
            processEngineConfiguration.getExecutionEntityManager().deleteProcessInstance(processInstanceId, deleteReason, true);
        }
        return null;
    }
}
