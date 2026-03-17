package org.example.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ChatConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    private final static String DEEPSEEK = "deepseek-v3";
    private final static String QWEN = "qwen-max";

    @Bean(name = "dsModel")
    public ChatModel dsModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(DEEPSEEK).build())
                .build();
    }

    @Bean(name = "qwModel")
    public ChatModel qwModel() {
        return DashScopeChatModel.builder()
                .dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(QWEN).build())
                .build();
    }

    @Bean(name = "dsClient")
    public ChatClient dsClient(@Qualifier("dsModel") ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean(name = "qwClient")
    public ChatClient qwClient(@Qualifier("qwModel") ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
}