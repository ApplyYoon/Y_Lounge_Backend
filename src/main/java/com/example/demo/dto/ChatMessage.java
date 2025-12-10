package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private String type; // CHAT, JOIN, LEAVE
    private String content;
    private String sender;
    private String roomId;
    private long timestamp;

    public ChatMessage(String type, String content, String sender, String roomId) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.roomId = roomId;
        this.timestamp = System.currentTimeMillis();
    }
}
