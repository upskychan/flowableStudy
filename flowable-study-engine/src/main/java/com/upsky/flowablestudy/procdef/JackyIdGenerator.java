package com.upsky.flowablestudy.procdef;

import org.flowable.common.engine.impl.cfg.IdGenerator;

import java.util.UUID;

/**
 * 自定义ID生成器。
 */
public class JackyIdGenerator implements IdGenerator {
    @Override
    public String getNextId() {
        return "Jacky-" + UUID.randomUUID().toString();
    }
}
