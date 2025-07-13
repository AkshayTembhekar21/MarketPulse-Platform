
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
public class TradeWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("✅ WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("❌ WebSocket connection closed: " + session.getId());
    }

    public void broadcastTrade(Object trade) {
        try {
            String message = objectMapper.writeValueAsString(trade);
            TextMessage textMessage = new TextMessage(message);
            
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                        return false;
                    }
                } catch (Exception e) {
                    System.err.println("Error sending message to session: " + e.getMessage());
                }
                return true;
            });
        } catch (Exception e) {
            System.err.println("Error broadcasting trade: " + e.getMessage());
        }
    }
}
