package com.example.api.repository.api;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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


    public String requestChatGpt(List<ChatMessage> messageList) throws Exception {
        return this.requestChatGpt(messageList, 150, 1d);
    }
    
    public String requestChatGpt(List<ChatMessage> messageList, int maxTokens, double temperature) throws Exception {
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
                    break;
                } else {
                    Thread.sleep(retrySleepMs);
                    retryCount++;
                }
            }
        }
        service.shutdownExecutor();
        return response;
    }
}
