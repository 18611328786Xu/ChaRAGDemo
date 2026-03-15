package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController("/qw")
public class QwController {


    @Resource(name = "qwModel")
    private ChatModel chatModel;


    @RequestMapping(value = "/qwHello", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public Flux<String> qwHello(@RequestParam(defaultValue = "分析一下下周一黄金现货的走势", name = "msg") String msg) throws Exception {
        SystemMessage systemMessage = new SystemMessage("你是一个专业的股票分析师，特别是对于中国的A股有着独到的见解，以及比较关注各种政策以及舆论等");
        UserMessage userMessage = new UserMessage(msg);
        Prompt prompt = new Prompt(systemMessage, userMessage);
        return chatModel.stream(prompt).map(ChatResponse -> {
                    return ChatResponse.getResult().getOutput().getText();
                });

    }


    @RequestMapping("/qw111")
    public Flux<String> qw111() throws Exception {

        SystemMessage systemMessage = new SystemMessage("你是一个专业的股票分析师，特别是对于中国的A股有着独到的见解，以及比较关注各种政策以及舆论等");
        UserMessage userMessage = new UserMessage("分析一下下周一福耀玻璃，美的，紫金矿业，包钢股份，隆基绿能能不能建仓，以及走势");
        List<Message> messages = Arrays.asList(systemMessage, userMessage);
        Prompt prompt = new Prompt(messages);
        return chatModel.stream(prompt).map(s -> {
            return s.getResult().getOutput().getText();
        });
    }


}
