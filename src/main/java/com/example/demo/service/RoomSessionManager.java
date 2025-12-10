package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomSessionManager {

    // Map<RoomId, Set<Username>>
    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    // Map<SessionId, RoomId> (to handle disconnects)
    private final Map<String, String> sessionRoomMap = new ConcurrentHashMap<>();

    // Map<SessionId, Username>
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    public void addUser(String roomId, String username, String sessionId) {
        roomUsers.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>())).add(username);
        sessionRoomMap.put(sessionId, roomId);
        sessionUserMap.put(sessionId, username);
    }

    public void removeUser(String sessionId) {
        String roomId = sessionRoomMap.remove(sessionId);
        String username = sessionUserMap.remove(sessionId);

        if (roomId != null && username != null) {
            Set<String> users = roomUsers.get(roomId);
            if (users != null) {
                users.remove(username);
                if (users.isEmpty()) {
                    roomUsers.remove(roomId);
                }
            }
        }
    }

    public String getRoomId(String sessionId) {
        return sessionRoomMap.get(sessionId);
    }

    public String getUsername(String sessionId) {
        return sessionUserMap.get(sessionId);
    }

    public Set<String> getUsersInRoom(String roomId) {
        return roomUsers.getOrDefault(roomId, Collections.emptySet());
    }
}
