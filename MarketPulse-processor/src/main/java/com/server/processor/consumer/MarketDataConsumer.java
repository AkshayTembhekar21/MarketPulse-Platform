package com.server.processor.consumer;

import com.server.processor.service.TradeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MarketDataConsumer {
    @Autowired
    private TradeService tradeService;

    @PostConstruct
    public void init() {
        // Kafka Consumer initialized and waiting for messages
    }


    @KafkaListener(topics = "market-data", groupId = "market-data-consumer-group")
    public void consume(String message) {
        tradeService.processMessage(message);
        // TODO: parse JSON, calculate price change, filter, save trade if needed
    }
}