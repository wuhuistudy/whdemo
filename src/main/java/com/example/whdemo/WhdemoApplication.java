package com.example.whdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.whdemo.mapper")
public class WhdemoApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(WhdemoApplication.class, args);
    }

}
