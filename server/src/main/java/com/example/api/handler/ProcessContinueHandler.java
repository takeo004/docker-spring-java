package com.example.api.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.api.constant.State;
import com.example.api.entity.UserInfo;
import com.example.api.entity.UserState;
import com.example.api.service.AdminService;
@Component
public class ProcessContinueHandler {
    
    @Autowired
    private AdminService adminService;

    public String handleContinueProcess(UserInfo userInfo, UserState userState, String message) throws Exception {
        State state = State.of(userState.getState(), userState.getStateDetail());
        switch(state) {
            case SET_CALENDER_ID_CONFIRM:
                return adminService.setCalenderId(message, userInfo, userState);
        }
        return "エラーが発生したよ！管理者に連絡してね！";
    }
}
