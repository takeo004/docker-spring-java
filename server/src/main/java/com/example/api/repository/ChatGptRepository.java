package com.example.api.repository;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

@Repository
public class ChatGptRepository {

    @Value("${chatgpt.token}")
    public String token;

    public String requestChatGpt(List<ChatMessage> messageList) throws Exception {
        return this.requestChatGpt(messageList, 150, 1d);
    }
    
    public String requestChatGpt(List<ChatMessage> messageList, int maxTokens, double temperature) throws Exception {
        OpenAiService service = new OpenAiService(token, Duration.ofSeconds(30));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messageList)
            .n(1)
            .maxTokens(maxTokens)
            .temperature(temperature)
            .stream(false)
            .build();

        service.shutdownExecutor();
        ChatCompletionResult result = service.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
}
