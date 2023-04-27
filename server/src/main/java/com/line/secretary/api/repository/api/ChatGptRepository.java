package com.line.secretary.api.repository.api;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.line.secretary.api.entity.UserInfo;
import com.line.secretary.api.service.LineMessageService;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;


@Repository
public class ChatGptRepository {

    @Value("${chatgpt.token}")
    public String token;
    @Value("${chatgpt.model}")
    public String model;
    @Value("${chatgpt.max-retry}")
    public int maxRetry;
    @Value("${chatgpt.retry-sleep-ms}")
    public long retrySleepMs;
    
    @Autowired
    public LineMessageService lineMessageService;

    public String requestChatGpt(List<ChatMessage> messageList, UserInfo userInfo) throws Exception {
        return this.requestChatGpt(messageList, userInfo, 1024, 1d);
    }
    
    public String requestChatGpt(List<ChatMessage> messageList, UserInfo userInfo, int maxTokens, double temperature) throws Exception {
        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30));
        ChatCompletionRequest request = ChatCompletionRequest.builder()
        .model(model)
        .messages(messageList)
        .n(1)
        .maxTokens(maxTokens)
        .temperature(temperature)
        .stream(false)
        .build();
        
        String response;
        int retryCount = 0;
        while(true) {
            try {
                response = service.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
            } catch (OpenAiHttpException e) {
                if(retryCount == maxRetry) {
                    response = "エラーが発生しました！繰り返す場合は管理者にご連絡ください！";
                    e.printStackTrace();
                    break;
                } else {
                    if(retryCount == 2) {
                        lineMessageService.pushLineMesage(userInfo.getLineUserId(), ".。o(読み込み中)");
                    }
                    Thread.sleep(retrySleepMs);
                    retryCount++;
                    continue;
                }
            }
            break;
        }
        service.shutdownExecutor();
        return response;
    }
}
