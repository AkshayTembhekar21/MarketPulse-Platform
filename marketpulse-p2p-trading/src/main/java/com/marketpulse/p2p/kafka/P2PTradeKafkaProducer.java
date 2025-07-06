package com.marketpulse.p2p.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketpulse.p2p.model.P2PTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class P2PTradeKafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendTradeRequest(P2PTrade trade) {
        sendMessage("p2p-trade-requests", trade);
    }

    public void sendTradeNegotiation(P2PTrade trade) {
        sendMessage("p2p-trade-negotiations", trade);
    }

    public void sendTradeConfirmation(P2PTrade trade) {
        sendMessage("p2p-trade-confirmations", trade);
    }

    private void sendMessage(String topic, P2PTrade trade) {
        try {
            String message = objectMapper.writeValueAsString(trade);
            kafkaTemplate.send(topic, trade.getId().toString(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize trade for Kafka", e);
        }
    }
} 