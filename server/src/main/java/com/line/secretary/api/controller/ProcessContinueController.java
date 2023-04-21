package com.line.secretary.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.line.secretary.api.constant.State;
import com.line.secretary.api.controller.request.googlecalendar.GoogleCalendarRequest;
import com.line.secretary.api.entity.UserInfo;
import com.line.secretary.api.entity.UserState;
import com.line.secretary.api.service.AdminService;
import com.line.secretary.api.service.ChatGptService;
import com.line.secretary.api.service.GoogleCalendarService;
import com.line.secretary.api.service.UserService;

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
