package com.marketpulse.p2p.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class P2PTradeKafkaConsumer {
    @KafkaListener(topics = "p2p-trade-requests", groupId = "p2p-trading-group")
    public void listenTradeRequests(String message) {
        System.out.println("[Kafka] Received trade request: " + message);
    }

    @KafkaListener(topics = "p2p-trade-negotiations", groupId = "p2p-trading-group")
    public void listenTradeNegotiations(String message) {
        System.out.println("[Kafka] Received trade negotiation: " + message);
    }

    @KafkaListener(topics = "p2p-trade-confirmations", groupId = "p2p-trading-group")
    public void listenTradeConfirmations(String message) {
        System.out.println("[Kafka] Received trade confirmation: " + message);
    }
} 