package com.example.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.api.constant.State;
import com.example.api.entity.UserInfo;
import com.example.api.entity.UserState;
import com.example.api.service.AdminService;
import com.example.api.service.ChatGptService;

@Controller
public class ProcessContinueController extends BaseController {
    
    @Autowired
    private AdminService adminService;
    @Autowired
    private ChatGptService chatGptService;

    private static final String notContinueResponse = "承知しました！処理を中断します";

    public String handleContinueProcess(UserInfo userInfo, UserState userState, String message) throws Exception {
        State state = State.of(userState.getState(), userState.getStateDetail());
        switch(state) {
            case SET_CALENDER_ID_CONFIRM:
                return checkProcessContinueByMessage(message, userInfo) 
                    ? adminService.setCalenderId(message, userInfo, userState) : notContinueResponse;

        }
        return "エラーが発生したよ！管理者に連絡してね！";
    }

    private boolean checkProcessContinueByMessage(String message, UserInfo userInfo) throws Exception {
        return chatGptService.messageIsYes(message, userInfo);
    }
}
