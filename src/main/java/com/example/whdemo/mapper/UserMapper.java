package com.example.whdemo.mapper;

import com.example.whdemo.bean.UserBean;

public interface UserMapper {
    UserBean getInfo(String name, String password);
    Integer addUSer(String name, String password);
}
