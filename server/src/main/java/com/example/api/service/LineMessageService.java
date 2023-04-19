package com.example.api.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;

@Service
public class LineMessageService {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    public BotApiResponse replyLineMessage(String replyToken, String replyMessage) throws InterruptedException, ExecutionException {
        return lineMessagingClient.replyMessage(
            new ReplyMessage(replyToken, new TextMessage(replyMessage), true)).get();
    }    
}
