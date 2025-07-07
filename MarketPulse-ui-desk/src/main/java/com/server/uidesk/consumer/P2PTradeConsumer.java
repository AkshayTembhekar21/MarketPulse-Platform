package com.server.uidesk.consumer;

import com.server.uidesk.websocket.P2PWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class P2PTradeConsumer {

    @Autowired
    private P2PWebSocketHandler p2pWebSocketHandler;

    @KafkaListener(topics = "p2p-trade-requests", groupId = "ui-desk-p2p-consumer-group")
    public void consumeP2PTradeRequest(String message) {
        System.out.println("🤝 Received P2P trade request: " + message);
        p2pWebSocketHandler.broadcastP2PUpdate(message);
    }

    @KafkaListener(topics = "p2p-trade-negotiations", groupId = "ui-desk-p2p-consumer-group")
    public void consumeP2PTradeNegotiation(String message) {
        System.out.println("💬 Received P2P trade negotiation: " + message);
        p2pWebSocketHandler.broadcastP2PUpdate(message);
    }

    @KafkaListener(topics = "p2p-trade-confirmations", groupId = "ui-desk-p2p-consumer-group")
    public void consumeP2PTradeConfirmation(String message) {
        System.out.println("✅ Received P2P trade confirmation: " + message);
        p2pWebSocketHandler.broadcastP2PUpdate(message);
    }
} 