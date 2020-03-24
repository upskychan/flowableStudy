package com.upsky.flowablestudy;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//扫描Service
@ComponentScan(basePackages = { "com.upsky.flowablestudy.**" })
//扫描Mapper
@MapperScan(basePackages = {"com.upsky.flowablestudy.**"})
@SpringBootApplication
public class FlowableStudyApp {
    private final static Logger logger = LoggerFactory.getLogger(FlowableStudyApp.class);

    public static void main(String[] args) {
        logger.info("开始启动服务 ");
        SpringApplication.run(FlowableStudyApp.class, args);
        logger.info("完成启动服务 ");
    }
}
