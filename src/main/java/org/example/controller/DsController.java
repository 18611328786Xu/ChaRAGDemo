package org.example.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/ds")
public class DsController {

    @Resource(name = "dsModel")
    private ChatModel chatModel;


    @RequestMapping("/dsHello")
    public String dsHello(@RequestParam(name = "msg", defaultValue = "hello") String msg) {

        SystemMessage systemMessage = new SystemMessage("你是一个智能机器人");
        UserMessage userMessage = new UserMessage(msg);
        Prompt prompt = new Prompt(userMessage, systemMessage);

        return chatModel.call(prompt).getResult().getOutput().getText();
    }

}
