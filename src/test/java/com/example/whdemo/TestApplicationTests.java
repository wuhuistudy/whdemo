package com.example.whdemo;
import com.example.whdemo.bean.UserBean;
import com.example.whdemo.service.UserService;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplicationTests {
    @Resource
    UserService userService;

    @Test
    public void contextLoads() {
        UserBean userBean = userService.loginIn("wuhui","wu123456");
        System.out.println("该用户ID为：");
        System.out.println(userBean.getId());
    }
}
