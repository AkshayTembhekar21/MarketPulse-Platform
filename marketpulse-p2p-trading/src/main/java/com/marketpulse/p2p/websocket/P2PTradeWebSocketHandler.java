package com.marketpulse.p2p.websocket;

import com.marketpulse.p2p.model.P2PTrade;
import com.marketpulse.p2p.service.P2PTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class P2PTradeWebSocketHandler {

    @Autowired
    private P2PTradeService tradeService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/trade.negotiate")
    @SendTo("/topic/trade.updates")
    public TradeMessage handleNegotiation(@Payload NegotiationRequest request) {
        try {
            P2PTrade updatedTrade = tradeService.negotiate(
                request.getTradeId(), 
                request.getUserId(), 
                request.getMessage()
            );
            
            // Send to specific users involved in the trade
            messagingTemplate.convertAndSendToUser(
                updatedTrade.getSenderId().toString(),
                "/queue/trade.updates",
                new TradeMessage("NEGOTIATION", updatedTrade)
            );
            
            messagingTemplate.convertAndSendToUser(
                updatedTrade.getReceiverId().toString(),
                "/queue/trade.updates",
                new TradeMessage("NEGOTIATION", updatedTrade)
            );
            
            return new TradeMessage("NEGOTIATION", updatedTrade);
        } catch (Exception e) {
            return new TradeMessage("ERROR", e.getMessage());
        }
    }

    @MessageMapping("/trade.accept")
    @SendTo("/topic/trade.updates")
    public TradeMessage handleAccept(@Payload TradeActionRequest request) {
        try {
            P2PTrade updatedTrade = tradeService.acceptTrade(
                request.getTradeId(), 
                request.getUserId()
            );
            
            // Notify both users
            notifyTradeParticipants(updatedTrade, "ACCEPTED");
            
            return new TradeMessage("ACCEPTED", updatedTrade);
        } catch (Exception e) {
            return new TradeMessage("ERROR", e.getMessage());
        }
    }

    @MessageMapping("/trade.reject")
    @SendTo("/topic/trade.updates")
    public TradeMessage handleReject(@Payload TradeActionRequest request) {
        try {
            P2PTrade updatedTrade = tradeService.rejectTrade(
                request.getTradeId(), 
                request.getUserId()
            );
            
            // Notify both users
            notifyTradeParticipants(updatedTrade, "REJECTED");
            
            return new TradeMessage("REJECTED", updatedTrade);
        } catch (Exception e) {
            return new TradeMessage("ERROR", e.getMessage());
        }
    }

    @MessageMapping("/trade.initiate")
    @SendTo("/topic/trade.updates")
    public TradeMessage handleInitiate(@Payload P2PTrade trade) {
        try {
            P2PTrade savedTrade = tradeService.initiateTrade(trade);
            
            // Notify the receiver
            messagingTemplate.convertAndSendToUser(
                savedTrade.getReceiverId().toString(),
                "/queue/trade.requests",
                new TradeMessage("NEW_REQUEST", savedTrade)
            );
            
            return new TradeMessage("INITIATED", savedTrade);
        } catch (Exception e) {
            return new TradeMessage("ERROR", e.getMessage());
        }
    }

    private void notifyTradeParticipants(P2PTrade trade, String action) {
        messagingTemplate.convertAndSendToUser(
            trade.getSenderId().toString(),
            "/queue/trade.updates",
            new TradeMessage(action, trade)
        );
        
        messagingTemplate.convertAndSendToUser(
            trade.getReceiverId().toString(),
            "/queue/trade.updates",
            new TradeMessage(action, trade)
        );
    }

    // Message classes for WebSocket communication
    public static class TradeMessage {
        private String type;
        private Object data;

        public TradeMessage(String type, Object data) {
            this.type = type;
            this.data = data;
        }

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }

    public static class NegotiationRequest {
        private Long tradeId;
        private Long userId;
        private String message;

        // Getters and setters
        public Long getTradeId() { return tradeId; }
        public void setTradeId(Long tradeId) { this.tradeId = tradeId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class TradeActionRequest {
        private Long tradeId;
        private Long userId;

        // Getters and setters
        public Long getTradeId() { return tradeId; }
        public void setTradeId(Long tradeId) { this.tradeId = tradeId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
} 