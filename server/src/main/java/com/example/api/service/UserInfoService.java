package com.example.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.entity.UserInfo;
import com.example.api.repository.db.UserInfoRepository;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoRepository userRepository;

    public List<UserInfo> getUsers() {
        return userRepository.findAll();
    }
}
