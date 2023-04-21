package com.example.api.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.api.controller.request.googlecalendar.GoogleCalendarRequest;
import com.example.api.entity.UserInfo;
import com.example.api.service.GoogleCalendarService;

@Controller
public class GoogleCalendarController extends BaseController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    public String registSchedule(UserInfo userInfo, String message) throws IOException, GeneralSecurityException {
        GoogleCalendarRequest request = super.generateRequest(GoogleCalendarRequest.class, message);
        return googleCalendarService.registSchedule(request, userInfo);
    }

    public String searchSchedule(UserInfo userInfo, String message) throws IOException, GeneralSecurityException {
        GoogleCalendarRequest request = super.generateRequest(GoogleCalendarRequest.class, message);
        return googleCalendarService.searchSchedule(request, userInfo);
    }

    public String deleteSchedule(UserInfo userInfo, String message) throws IOException, GeneralSecurityException {
        GoogleCalendarRequest request = super.generateRequest(GoogleCalendarRequest.class, message);
        return googleCalendarService.deleteSchedule(request, userInfo);   
    }
}
