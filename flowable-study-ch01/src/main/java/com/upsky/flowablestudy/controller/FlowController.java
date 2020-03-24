package com.upsky.flowablestudy.controller;

import com.upsky.flowablestudy.service.IFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/flow")
public class FlowController {
    private static final Logger log = LoggerFactory.getLogger(FlowController.class);

    /**
     * 流程处理服务
     */
    @Autowired
    private IFlowService flowService;

    @RequestMapping("/create")
    @ResponseBody
    public Map<String,String> createFlow(){
        Map<String,String> res =new HashMap<>();

        String flowPath ="E:\\test\\flowable\\测试BPMN模型.bpmn20.xml";

        if (null == flowService.createFlow(flowPath)){
            res.put("msg","创建流程失败");
            res.put("res","0");
            return res;
        }
        res.put("msg","创建流程成功");
        res.put("res","1");
        return res;
    }
}
