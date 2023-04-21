package com.example.api.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.example.api.constant.MethodDetailType;
import com.example.api.constant.MethodType;
import com.example.api.controller.ProcessContinueController;
import com.example.api.entity.UserInfo;
import com.example.api.entity.UserState;
import com.example.api.service.AdminService;
import com.example.api.service.ChatGptService;
import com.example.api.service.LineMessageService;
import com.example.api.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@LineMessageHandler
public class LineMessageWebhookHandler {

    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private ProcessContinueController continueController;
    @Autowired
    private LineMessageService lineMessageService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ChatGptService chatGptService;


    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws InterruptedException, ExecutionException {
        try{
            TextMessageContent message = event.getMessage();
            UserInfo userInfo = userService.findUserByLineUserId(event.getSource().getUserId());
           
            if(userInfo == null) {
                // ユーザー情報が取得できない場合は初期設定を行う
                lineMessageService.replyLineMessage(event.getReplyToken(),  userService.initUser(event.getSource().getUserId(), message.getText()));
                return;
            }

            UserState userState = userService.findUserStateByUserId(userInfo.getUserId());
            if(userState != null) {
                // 中断中の処理がある場合は、中断中の処理を再開する
                lineMessageService.replyLineMessage(event.getReplyToken(), continueController.handleContinueProcess(userInfo, userState, message.getText()));
                return;
            }

            if(message.getText().startsWith("--")) {
                // 管理用コマンドが入力された場合は、専用の処理を実行
                String adminResponse = adminService.adminProcess(message.getText(), userInfo);
                if(adminResponse != null) {
                    // 管理者かつ存在するコマンドが入力されていた場合はその結果を返す
                    lineMessageService.replyLineMessage(event.getReplyToken(), adminResponse);
                    return;
                }
            }

            // chatGPTをつかって、メッセージから処理内容を判定
            String chatGptResponse = chatGptService.checkMethodForrequestMessage(message.getText(), userInfo);
            
            MethodDetailType type = this.generateRequestMethod(chatGptResponse);
            if(type == null) {
                // 用意した機能外として、chatGptの回答をそのまま返信する（普通の会話になる想定）
                lineMessageService.replyLineMessage(event.getReplyToken(), chatGptResponse);
                return;
            }

            // リフレクションでコントローラと対象のメソッドを呼び出す
            Object classes = context.getBean(type.getMethod().getControllerName());
            Method method = classes.getClass().getMethod(type.getMethodName(), MethodDetailType.class, String.class, UserInfo.class);

            String response = method.invoke(classes, type, chatGptResponse, userInfo).toString();
            
            // コントローラの返却値をそのまま返信する
            lineMessageService.replyLineMessage(event.getReplyToken(), response);
        } catch(Exception e) {
            e.printStackTrace();
            lineMessageService.replyLineMessage(event.getReplyToken(), "エラーが発生しました！繰り返すようであれば管理者にご連絡をお願いします！");
        }
    }

    private MethodDetailType generateRequestMethod(String chatGptResponse) throws JsonMappingException, JsonProcessingException {
        if(!chatGptResponse.contains("{\"method\":")) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> requestMap = objectMapper.readValue(chatGptResponse, new TypeReference<HashMap<String, String>>() {});

        MethodType methodType = MethodType.of(requestMap.get("method"));
        if(methodType == null) {
            return null;
        }

        MethodDetailType methodDetailType = MethodDetailType.of(methodType, requestMap.get("methodDetail"));
        return methodDetailType;
    }
}
