package com.example.demo.config;

import com.example.demo.service.RoomSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    private RoomSessionManager roomSessionManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String roomId = roomSessionManager.getRoomId(sessionId);
        String username = roomSessionManager.getUsername(sessionId);

        if (roomId != null && username != null) {
            roomSessionManager.removeUser(sessionId);

            // Broadcast leave message
            Map<String, Object> message = new HashMap<>();
            message.put("type", "leave");
            message.put("sender", username);
            message.put("users", roomSessionManager.getUsersInRoom(roomId));

            messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
        }
    }
}
