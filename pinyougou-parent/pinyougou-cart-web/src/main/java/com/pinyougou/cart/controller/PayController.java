package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.pay.service.WeiXinPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeiXinPayService weiXinPayService;

    @RequestMapping("/createNative")
    public Map createNative() {
        //暂时获取随机字符串
        IdWorker idWorker = new IdWorker();
        return weiXinPayService.createNative(idWorker.nextId() + "", "1");
    }
}
