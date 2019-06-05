package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 用户名
     * @return
     */
    @RequestMapping("/name")
    public Map showName(){
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前用户名"+name);
        Map map=new HashMap<>();
        map.put("loginName",name);
        return map;
    }
}
