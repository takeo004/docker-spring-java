package com.example.api.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api.constant.AdminProcess;
import com.example.api.entity.UserInfo;
import com.example.api.repository.db.UserInfoRepository;

@Service
public class AdminService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public String adminProcess(String message, UserInfo adminUserInfo) {
        String response = null;
        if(!adminUserInfo.isAdminFlg()) {
            return response;
        } else if(message.startsWith(AdminProcess.HELP.getPrefix())) {
            return generateHelpMessage();
        } else if (message.startsWith(AdminProcess.CREATE_USER.getPrefix())) {
            response = this.createUser(message);
        } else if (message.startsWith(AdminProcess.SET_CALENDER_ID.getPrefix())) {
            response = this.setCalenderId(message);
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

    public String setCalenderId(String message) {
        message = message.replace(AdminProcess.SET_CALENDER_ID.getPrefix(), "");
        List<String> commands = Arrays.asList(message.split(" "));

        if(commands.size() != 2) {
            return "コマンドに不備があります。".concat(AdminProcess.SET_CALENDER_ID.getPrefix()).concat("[username] [calenderId]");
        }

        String userName = commands.get(0);
        String calenderId = commands.get(1);
        // カレンダー登録処理
        return "カレンダーIDの登録処理はまだ未実装！";
    }
}
