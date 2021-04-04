package com.example.whdemo.service;

import com.example.whdemo.bean.UserBean;

public interface UserService {
    UserBean loginIn(String name, String password);
    Integer addUSer(String name, String password);
}
