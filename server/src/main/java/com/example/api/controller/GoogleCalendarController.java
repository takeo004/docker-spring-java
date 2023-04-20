package com.example.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.api.constant.MethodDetailType;
import com.example.api.controller.request.googlecalendar.GoogleCalendarRegistRequest;
import com.example.api.controller.request.googlecalendar.GoogleCalendarSearchRequest;
import com.example.api.entity.UserInfo;
import com.example.api.service.GoogleCalendarService;

@Controller
public class GoogleCalendarController extends BaseController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    public String registSchedule(MethodDetailType type, String chatGptResponse, UserInfo userInfo) throws IOException, GeneralSecurityException {
        GoogleCalendarRegistRequest request = (GoogleCalendarRegistRequest) super.generateRequest(type, chatGptResponse);
        return googleCalendarService.registSchedule(request, userInfo);
    }

    public String searchSchedule(MethodDetailType type, String chatGptResponse, UserInfo userInfo) throws IOException, GeneralSecurityException {
        GoogleCalendarSearchRequest request = (GoogleCalendarSearchRequest) super.generateRequest(type, chatGptResponse);
        return googleCalendarService.searchSchedule(request, userInfo);
    }
}
