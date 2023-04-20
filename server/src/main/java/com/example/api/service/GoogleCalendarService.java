package com.example.api.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.controller.request.googlecalendar.GoogleCalendarRegistRequest;
import com.example.api.entity.GoogleUserInfo;
import com.example.api.entity.UserInfo;
import com.example.api.repository.api.GoogleCalendarRepository;
import com.example.api.repository.db.GoogleUserInfoRepository;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

@Service
public class GoogleCalendarService {

    @Autowired
    private GoogleUserInfoRepository googleUserInfoRepository;
    @Autowired
    private GoogleCalendarRepository googleCalendarRepository;
    
    public String registSchedule(GoogleCalendarRegistRequest request, UserInfo userInfo) throws FileNotFoundException, IOException, GeneralSecurityException {
        GoogleUserInfo googleUserInfo = googleUserInfoRepository.findById(userInfo.getUserId()).orElse(new GoogleUserInfo());
        if(googleUserInfo.getCalendarId() == null) {
            // カレンダーIDが取得できない場合は作成する
            this.addCalendar(userInfo, googleUserInfo);
        }
        Event event = googleCalendarRepository.requestRegisEvent(request, googleUserInfo);

        return "以下で登録したよ！\nタイトル：".concat(event.getSummary()).concat("\n日付：").concat(event.getStart().getDate().toString());
    }

    private void addCalendar(UserInfo userInfo, GoogleUserInfo googleUserInfo) throws IOException, GeneralSecurityException {
        Calendar calendar = googleCalendarRepository.requestAddCalendar(userInfo);
            
        googleUserInfo.setUserId(userInfo.getUserId());
        googleUserInfo.setCalendarId(calendar.getId());
        googleUserInfoRepository.save(googleUserInfo);
    }

    public String addCalendarRole(UserInfo userInfo, String userEmail) throws IOException, GeneralSecurityException {
        Optional<GoogleUserInfo> googleUserInfo = googleUserInfoRepository.findById(userInfo.getUserId());
        if(!googleUserInfo.isPresent()) {
            return "対象のカレンダーが存在しません！";
        }

        googleCalendarRepository.addCalendarRole(googleUserInfo.get(), userEmail);
        return userInfo.getUserName().concat("のカレンダーが以下のGoogleアカウントから見れるようになったよ！\n".concat(userEmail));
    }
}
