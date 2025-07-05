
package com.server.uidesk.consumer;

import com.server.uidesk.websocket.TradeWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeUpdateConsumer {

    @Autowired
    private TradeWebSocketHandler webSocketHandler;

    @KafkaListener(topics = "trade-updates", groupId = "ui-desk-consumer-group")
    public void consumeTradeUpdate(String message) {
        System.out.println("📊 Received trade update: " + message);
        // Forward the trade update to WebSocket clients
        webSocketHandler.broadcastTrade(message);
    }
}
