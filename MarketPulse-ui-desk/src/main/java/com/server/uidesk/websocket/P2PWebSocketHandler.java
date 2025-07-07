package com.server.uidesk.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class P2PWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("✅ P2P WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("❌ P2P WebSocket connection closed: " + session.getId());
    }

    public void broadcastP2PUpdate(Object p2pData) {
        try {
            System.out.println("📤 Broadcasting P2P data: " + p2pData);
            String message = objectMapper.writeValueAsString(p2pData);
            TextMessage textMessage = new TextMessage(message);
            
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                        return false;
                    }
                } catch (Exception e) {
                    System.err.println("Error sending P2P message to session: " + e.getMessage());
                }
                return true;
            });
        } catch (Exception e) {
            System.err.println("Error broadcasting P2P update: " + e.getMessage());
        }
    }
} 