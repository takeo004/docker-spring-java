package com.line.secretary.api.service;

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
import org.springframework.util.StringUtils;

import com.line.secretary.api.constant.State;
import com.line.secretary.api.controller.request.googlecalendar.GoogleCalendarRequest;
import com.line.secretary.api.entity.GoogleUserInfo;
import com.line.secretary.api.entity.UserInfo;
import com.line.secretary.api.entity.UserState;
import com.line.secretary.api.repository.api.GoogleCalendarRepository;
import com.line.secretary.api.repository.db.GoogleUserInfoRepository;
import com.line.secretary.api.repository.db.UserStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;

@Service
public class GoogleCalendarService {

    @Autowired
    private UserStateRepository userStateRepository;
    @Autowired
    private GoogleUserInfoRepository googleUserInfoRepository;
    @Autowired
    private GoogleCalendarRepository googleCalendarRepository;
    
    public String registSchedule(GoogleCalendarRequest request, UserInfo userInfo) throws FileNotFoundException, IOException, GeneralSecurityException {
        GoogleUserInfo googleUserInfo = this.getGoogleUserInfo(userInfo);
        Event event = googleCalendarRepository.requestRegisEvent(googleUserInfo.getCalendarId(), request.getStartDate(), request.getEndDate(), request.getTitle());

        return "以下で登録しました！\nタイトル：".concat(event.getSummary()).concat("\n日付：").concat(this.convertHaihunToSlash(event.getStart().getDate().toString()));
    }

    public String searchSchedule(GoogleCalendarRequest request, UserInfo userInfo) throws IOException, GeneralSecurityException {
        StringBuilder response = new StringBuilder()
            .append(convertHaihunToSlash(request.getStartDate()))
            .append("~")
            .append(convertHaihunToSlash(request.getEndDate()))
            .append("で検索しました！");

        GoogleUserInfo googleUserInfo = this.getGoogleUserInfo(userInfo);
        if(googleUserInfo == null) {
            response.append("\n予定はありませんでした！");
            return response.toString();
        }

        List<Event> eventList = googleCalendarRepository.requestSearchEvent(googleUserInfo.getCalendarId(), request.getStartDate(), request.getEndDate());

        this.generateSearchResponse(response, eventList);
        return response.toString();
    }

    private String convertHaihunToSlash(String target) {
        return target.replace("-", "/");
    }

    private void generateSearchResponse(StringBuilder response, List<Event> eventList) {
        if(eventList.isEmpty()) {
            response.append("\n予定はありませんでした！");
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
        Calendar calendar = googleCalendarRepository.requestAddCalendar(userInfo.getUserName());
            
        googleUserInfo.setUserId(userInfo.getUserId());
        googleUserInfo.setCalendarId(calendar.getId());
        googleUserInfoRepository.save(googleUserInfo);
    }

    public String deleteSchedule(GoogleCalendarRequest request, UserInfo userInfo) throws IOException, GeneralSecurityException {
        GoogleUserInfo googleUserInfo = googleUserInfoRepository.findById(userInfo.getUserId()).orElse(null);
        StringBuilder response = new StringBuilder();

        List<Event> eventList = null;
        if(googleUserInfo != null) {
            eventList = googleCalendarRepository.requestSearchEvent(googleUserInfo.getCalendarId(), request.getStartDate(), request.getEndDate(), request.getTitle());
        }
        if(eventList == null || eventList.isEmpty()) {
            response.append("以下で検索したけど対象の予定は見つかりませんでした！");
            if(StringUtils.hasText(request.getStartDate())) {
                response.append("\n日付：")
                .append(convertHaihunToSlash(request.getStartDate()))
                .append("~")
                .append(convertHaihunToSlash(request.getEndDate()));
            }
            if(StringUtils.hasText(request.getTitle())) {
                response.append("\nタイトル：")
                    .append(request.getTitle());
            }
        } else {
            response.append("以下の予定を削除してもよろしいですか？");
            this.generateSearchResponse(response, eventList);
            userStateRepository.save(new UserState(
                userInfo.getUserId(),
                State.DELETE_SCHEDULE_CONFIRM,
                new ObjectMapper().writeValueAsString(request)));
        }


        return response.toString();
    }

    public String deleteSchedule(GoogleCalendarRequest request, UserInfo userInfo, UserState userState) throws IOException, GeneralSecurityException {
        GoogleUserInfo googleUserInfo = googleUserInfoRepository.findById(userInfo.getUserId()).orElse(null);

        List<Event> eventList = googleCalendarRepository.requestSearchEvent(googleUserInfo.getCalendarId(), request.getStartDate(), request.getEndDate(), request.getTitle());
        googleCalendarRepository.requestDeleteEvent(googleUserInfo.getCalendarId(), eventList);

        return "削除しました！";
    }

    public String addCalendarRole(UserInfo userInfo, String userEmail) throws IOException, GeneralSecurityException {
        Optional<GoogleUserInfo> googleUserInfo = googleUserInfoRepository.findById(userInfo.getUserId());
        if(!googleUserInfo.isPresent()) {
            return "対象のカレンダーが存在しません！";
        }

        googleCalendarRepository.addCalendarRole(googleUserInfo.get().getCalendarId(), userEmail);;
        return userInfo.getUserName().concat("のカレンダーが以下のGoogleアカウントから見れるようになりました！\n".concat(userEmail));
    }
}
