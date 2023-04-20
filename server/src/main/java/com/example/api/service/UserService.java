package com.example.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.entity.UserInfo;
import com.example.api.entity.UserInitState;
import com.example.api.entity.UserState;
import com.example.api.repository.db.UserInfoRepository;
import com.example.api.repository.db.UserInitStateRepository;
import com.example.api.repository.db.UserStateRepository;

@Service
public class UserService {
    @Autowired
    private UserInfoRepository userRepository;
    @Autowired
    private UserInitStateRepository userInitStateRepository;
    @Autowired
    private UserStateRepository userStateRepository;

    public UserInfo findUserByLineUserId(String lineUserId) {
        return userRepository.findByLineUserId(lineUserId);
    }

    public UserState findUserStateByUserId(int userId) {
        return userStateRepository.findById(userId).orElse(null);
    }

    public String initUser(String lineUserId, String message) {
        UserInitState initState = userInitStateRepository.findByLineUserId(lineUserId);
        if(initState == null) {
            userInitStateRepository.save(new UserInitState(lineUserId));
            return "管理者が設定した名前を入力してください！";
        } else {
            UserInfo userInfo = userRepository.findByUserName(message);
            if(userInfo == null) {
                return "名前が登録されていません！管理者に再度確認してください！";
            } else if(userInfo.getLineUserId() != null) {
                return "初期設定済みの名前です！管理者に再度確認してください！";
            } else {
                userInfo.setLineUserId(lineUserId);
                userRepository.save(userInfo);
                userInitStateRepository.delete(userInitStateRepository.findByLineUserId(lineUserId));
                return "初期設定が完了しました！";
            }
        }
    }
}
