package com.example.api;

import java.io.StringWriter;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.api.constant.MethodDetailType;
import com.example.api.constant.MethodType;
import com.example.api.controller.request.LineRequestBase;
import com.example.api.controller.request.LineScheduleDeleteRequest;
import com.example.api.controller.request.LineScheduleRegistRequest;
import com.example.api.controller.request.LineScheduleSearchRequest;
import com.example.api.service.ChatGptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@LineMessageHandler
public class LineMessageWebhookHandler {
    
    @Autowired
    private LineMessagingClient lineMessagingClient;
    @Autowired
    private ChatGptService chatGptService;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();

        String chatGptResponse = chatGptService.checkMethodForrequestMessage(message.getText());
        LineRequestBase request = this.generateLineRequest(chatGptResponse);

        if(request == null) {
            // システムで用意した機能外として、chatGptの回答をそのまま返信する
            lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(chatGptResponse), true)).get();
            return;
        }

        // テスト用
        StringWriter writer = new StringWriter();
        new ObjectMapper()
        .registerModule(new JavaTimeModule()) // JSR-310 サポート
        .writerWithDefaultPrettyPrinter() // 読みやすく整形
        .writeValue(writer, request);

        // 返信
        lineMessagingClient.replyMessage(new ReplyMessage(event.getReplyToken(), new TextMessage(writer.toString()), true)).get();
    }

    private LineRequestBase generateLineRequest(String chatGptResponse) throws JsonMappingException, JsonProcessingException {
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
        LineRequestBase request;
        switch(methodDetailType.getMethod()) {
            case SCHEDULE:
                request = this.generateLineScheduleRequestBase(methodDetailType, chatGptResponse);
                break;
            default:
                return null;
        }
        
        request.setMethodDetailType(methodDetailType);
        return request;
    }

    private LineRequestBase generateLineScheduleRequestBase(MethodDetailType methodDetailType, String chatGptResponse) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        switch(methodDetailType) {
            case SCHEDULE_REGIST:
                return mapper.readValue(chatGptResponse, LineScheduleRegistRequest.class);
            case SCHEDULE_SEARCH:
                return mapper.readValue(chatGptResponse, LineScheduleSearchRequest.class);
            case SCHEDULE_DELETE:
                return mapper.readValue(chatGptResponse, LineScheduleDeleteRequest.class);
            default:
                return null;
        }
    }
}
