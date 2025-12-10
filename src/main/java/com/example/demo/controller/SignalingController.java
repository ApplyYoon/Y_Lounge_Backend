package com.example.demo.controller;

import com.example.demo.service.RoomSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller

public class SignalingController {

    @Autowired
    private RoomSessionManager roomSessionManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Helper class for signal messages
    public static class SignalMessage {
        public String type;
        public String targetUser;
        public Object data;
        public String sender;
        public Set<String> users; // Add users list to payload
        public Integer level; // Fire level for game mechanics
    }

    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public SignalMessage joinRoom(@DestinationVariable String roomId, @Payload SignalMessage message,
            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        roomSessionManager.addUser(roomId, message.sender, sessionId);

        message.type = "join";
        message.users = roomSessionManager.getUsersInRoom(roomId);
        return message;
    }

    @MessageMapping("/signal/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public SignalMessage signal(@DestinationVariable String roomId, @Payload SignalMessage message) {
        return message;
    }

    // REST Endpoint to fetch users (optional, for initial load)
    @ResponseBody
    @GetMapping("/api/rooms/{roomId}/users")
    public Set<String> getRoomUsers(@PathVariable String roomId) {
        return roomSessionManager.getUsersInRoom(roomId);
    }
}
