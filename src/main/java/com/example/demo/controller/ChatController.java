package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller

public class ChatController {

    @MessageMapping("/chat/{roomId}/sendMessage")
    @SendTo("/topic/room/{roomId}/chat")
    public ChatMessage sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(System.currentTimeMillis());
        return chatMessage;
    }
}
