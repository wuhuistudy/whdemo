package com.example.whdemo.serviceImpl;

import com.example.whdemo.bean.UserBean;
import com.example.whdemo.mapper.UserMapper;
import com.example.whdemo.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserBean loginIn(String name, String password) {
        return userMapper.getInfo(name,password);
    }

    @Override
    public Integer addUSer(String name, String password) {
        return userMapper.addUSer(name,password);
    }
}
