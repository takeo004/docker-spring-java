package com.example.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.api.entity.UserInfo;
import com.example.api.service.UserInfoService;

@Controller
@RequestMapping("/users")
public class UserInfoController {
    @Autowired
    private UserInfoService userService;

    public String index(Model model) {
        List<UserInfo> userList = userService.getUsers();
        model.addAttribute("users", userList);
        return "users/index";
    }
}