package com.upsky.flowablestudy.service;

import org.flowable.engine.repository.Deployment;

public interface IFlowService {
    /**
     * 部署工作流
     */
    Deployment createFlow(String filePath);
}
