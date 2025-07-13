package com.server.processor.service;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PricePublisher {

    private final AtomicReference<String> latestPrice = new AtomicReference<>(null);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void startScheduledPublishing() {
        scheduler.scheduleAtFixedRate(() -> {
            String priceToSend = latestPrice.getAndSet(null);
            if (priceToSend != null) {
                kafkaTemplate.send("trade-updates", priceToSend);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void updateLatestPrice(String price) {
        latestPrice.set(price);
    }
}

