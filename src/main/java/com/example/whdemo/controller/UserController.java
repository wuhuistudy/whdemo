package com.example.whdemo.controller;

import com.example.whdemo.bean.UserBean;
import com.example.whdemo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    UserService userService;
    @RequestMapping(value = "/addUSer",method = RequestMethod.POST)
    public String addUSer(String name,String password){
        Integer integer = userService.addUSer(name, password);
        if (logger.isDebugEnabled())
        {
            logger.debug("integer = " + integer);
        }
        System.out.println("integer = " + integer);
        if(integer != null){
            return "success";
        }else {
            return "error";
        }
    }
}
