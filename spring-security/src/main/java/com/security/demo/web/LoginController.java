package com.security.demo.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class LoginController {


    @PostMapping("/login")
    public String login() {
        return "登录成功";
    }

}
