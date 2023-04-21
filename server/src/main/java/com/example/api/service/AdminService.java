package com.example.api.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.constant.AdminProcess;
import com.example.api.constant.State;
import com.example.api.entity.GoogleUserInfo;
import com.example.api.entity.UserInfo;
import com.example.api.entity.UserState;
import com.example.api.repository.db.GoogleUserInfoRepository;
import com.example.api.repository.db.UserInfoRepository;
import com.example.api.repository.db.UserStateRepository;

@Service
public class AdminService {

    @Autowired
    private ChatGptService chatGptService;
    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserStateRepository userStateRepository;
    @Autowired
    private GoogleUserInfoRepository googleUserInfoRepository;
    
    public String adminProcess(String message, UserInfo adminUserInfo) throws Exception {
        String response = null;
        if(!adminUserInfo.isAdminFlg()) {
            return response;
        } else if(message.startsWith(AdminProcess.HELP.getPrefix())) {
            return generateHelpMessage();
        } else if (message.startsWith(AdminProcess.CREATE_USER.getPrefix())) {
            response = this.createUser(message);
        } else if (message.startsWith(AdminProcess.SET_CALENDAR_ID.getPrefix())) {
            response = this.setCalenderId(message, adminUserInfo);
        } else if (message.startsWith(AdminProcess.ADD_CALENDAR_ROLE.getPrefix())) {
            response = this.addCalendarRole(message, adminUserInfo);
        }
        return response;        
    }

    private String generateHelpMessage() {
        StringBuilder builder = new StringBuilder();
        Arrays.asList(AdminProcess.values()).stream()
            .forEach(process -> builder.append(process.getDescription() + "\n"));
        return builder.toString();
    }

    private String createUser(String message) {
        message = message.replace(AdminProcess.CREATE_USER.getPrefix(), "");
        if(message.replace(" ", "").equals("")
            || userInfoRepository.findByUserName(message) != null) {
            return "登録済みもしくは使用できないユーザー名です。";
        }

        userInfoRepository.save(new UserInfo(0, message, null, false));
        return message.concat(" で新規ユーザーを登録しました。");
    }

    public String setCalenderId(String message, UserInfo userInfo) throws Exception {
        return this.setCalenderId(message, userInfo, null);
    }

    public String setCalenderId(String message, UserInfo userInfo, UserState userState) throws Exception {

        String userName;
        String calenderId;
        if(userState == null) {
            message = message.replace(AdminProcess.SET_CALENDAR_ID.getPrefix(), "");
            List<String> commands = Arrays.asList(message.split(" "));
    
            if(commands.size() != 2) {
                return "コマンドに不備があります。".concat(AdminProcess.SET_CALENDAR_ID.getDescription());
            }
            userName = commands.get(0);
            calenderId = commands.get(1);
        } else {
            List<String> notes = Arrays.asList(userState.getNote().split(","));
            userName = notes.get(0);
            calenderId = notes.get(1);
        }

        UserInfo targetUserInfo = userInfoRepository.findByUserName(userName);
        if(targetUserInfo == null) {
            return "存在しないユーザー名です。";
        }
        
        GoogleUserInfo googleUserInfo = googleUserInfoRepository.findById(targetUserInfo.getUserId()).orElse(new GoogleUserInfo());
        if(userState == null && googleUserInfo.getCalendarId() != null) {
            userStateRepository.save(new UserState(
                userInfo.getUserId(),
                State.SET_CALENDER_ID_CONFIRM.getState(),
                State.SET_CALENDER_ID_CONFIRM.getDetail(),
                targetUserInfo.getUserName().concat(",").concat(calenderId),
                 new Date()));
            return "既にカレンダーIDが設定されていますが、上書きしてもよろしいですか？\nカレンダーID：" + googleUserInfo.getCalendarId();
        }

        googleUserInfo.setUserId(targetUserInfo.getUserId());
        googleUserInfo.setCalendarId(calenderId);
        googleUserInfoRepository.save(googleUserInfo);

        if(userState != null) {
            userStateRepository.delete(userState);
        }
        return "カレンダーIDの登録が完了しました！";
    }

    private String addCalendarRole(String message, UserInfo userInfo) throws IOException, GeneralSecurityException {
        message = message.replace(AdminProcess.ADD_CALENDAR_ROLE.getPrefix(), "");
        List<String> commands = Arrays.asList(message.split(" "));
        
        if(commands.size() != 2) {
            return "コマンドに不備があります。".concat(AdminProcess.ADD_CALENDAR_ROLE.getDescription());
        }

        String userName = commands.get(0);
        String userEmail = commands.get(1);

        UserInfo targetUserInfo = userInfoRepository.findByUserName(userName);
        if(targetUserInfo == null) {
            return "存在しないユーザー名です。";
        }

        return googleCalendarService.addCalendarRole(targetUserInfo, userEmail);
    }
}
