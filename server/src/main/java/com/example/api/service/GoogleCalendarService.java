package com.example.api.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.controller.request.googlecalendar.GoogleCalendarRegistRequest;
import com.example.api.controller.request.googlecalendar.GoogleCalendarSearchRequest;
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
        GoogleUserInfo googleUserInfo = this.getGoogleUserInfo(userInfo);
        Event event = googleCalendarRepository.requestRegisEvent(request, googleUserInfo);

        return "以下で登録したよ！\nタイトル：".concat(event.getSummary()).concat("\n日付：").concat(this.convertHaihunToSlash(event.getStart().getDate().toString()));
    }

    public String searchSchedule(GoogleCalendarSearchRequest request, UserInfo userInfo) throws IOException, GeneralSecurityException {
        StringBuilder response = new StringBuilder()
            .append(convertHaihunToSlash(request.getStartDate()))
            .append("~")
            .append(convertHaihunToSlash(request.getEndDate()))
            .append("で検索したよ！");

        GoogleUserInfo googleUserInfo = this.getGoogleUserInfo(userInfo);
        List<Event> eventList = googleCalendarRepository.requestSearchEvent(googleUserInfo, request);

        this.generateSearchResponse(response, eventList);
        return response.toString();
    }

    private String convertHaihunToSlash(String target) {
        return target.replace("-", "/");
    }

    private void generateSearchResponse(StringBuilder response, List<Event> eventList) {
        if(eventList.isEmpty()) {
            response.append("予定は無かったよ！");
        } else {
            Map<String, List<String>> eventMap = new LinkedHashMap<>();
            eventList.stream()
                .forEach(event -> {
                    String date = event.getStart().getDate().toString();
                    List<String> events = eventMap.get(date);

                    if(events == null) {
                        events = new ArrayList<String>();
                        events.add(event.getSummary());
                        eventMap.put(date, events);
                    } else {
                        events.add(event.getSummary());
                    }
                });

            eventMap.entrySet().stream()
                .forEach(entry -> {
                    response.append("\n\n日付：")
                        .append(convertHaihunToSlash(entry.getKey()));
                    
                    AtomicInteger index = new AtomicInteger();
                    entry.getValue().stream()
                        .forEach(event -> {
                            response.append("\n")
                                .append(index.incrementAndGet())
                                .append(". ")
                                .append(event);
                        });
                });
        }
    }

    private GoogleUserInfo getGoogleUserInfo(UserInfo userInfo) throws IOException, GeneralSecurityException {
        GoogleUserInfo googleUserInfo = googleUserInfoRepository.findById(userInfo.getUserId()).orElse(new GoogleUserInfo());
        if(googleUserInfo.getCalendarId() == null) {
            // カレンダーIDが取得できない場合は作成する
            this.addCalendar(userInfo, googleUserInfo);
        }

        return googleUserInfo;
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
