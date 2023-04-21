package com.example.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.api.constant.State;
import com.example.api.controller.request.googlecalendar.GoogleCalendarRequest;
import com.example.api.entity.UserInfo;
import com.example.api.entity.UserState;
import com.example.api.service.AdminService;
import com.example.api.service.ChatGptService;
import com.example.api.service.GoogleCalendarService;
import com.example.api.service.UserService;

@Controller
public class ProcessContinueController extends BaseController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private ChatGptService chatGptService;
    @Autowired
    private GoogleCalendarService googleCalendarService;
    @Autowired
    private AdminService adminService;

    private static final String notContinueResponse = "直前の処理を中断します！";

    public String handleContinueProcess(UserInfo userInfo, UserState userState, String message) throws Exception {
        State state = State.of(userState.getState(), userState.getStateDetail());
        String response = null;
        switch(state) {
            case SET_CALENDER_ID_CONFIRM:
                response = checkProcessContinueByMessage(message, userInfo) 
                    ? adminService.setCalenderId(message, userInfo, userState) : notContinueResponse;
                break;
            case DELETE_SCHEDULE_CONFIRM:
                response = checkProcessContinueByMessage(message, userInfo)
                    ? googleCalendarService.deleteSchedule(super.generateRequest(GoogleCalendarRequest.class, userState.getNote()), userInfo, userState) : notContinueResponse;
                break;
            default:
                response = "エラーが発生しました！管理者に連絡してください！";
        }

        userService.deleteUserState(userState);
        return response;
    }

    private boolean checkProcessContinueByMessage(String message, UserInfo userInfo) throws Exception {
        return chatGptService.messageIsYes(message, userInfo);
    }
}
