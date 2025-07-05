package com.server.processor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.processor.model.Trade;
import com.server.processor.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PricePublisher pricePublisher;


    private double lastPrice = 0.0;

    public void processMessage(String message) {
        try {
            JsonNode jsonNode = mapper.readTree(message);

            if (jsonNode.has("data")) {
                for (JsonNode node : jsonNode.get("data")) {
                    String ticker = node.get("s").asText();
                    double price = node.get("p").asDouble();
                    long ts = node.get("t").asLong();

                    lastPrice = price;

                    Trade trade = this.createTrade(ticker, price, ts);
                    //tradeRepository.save(trade);

                    // Publish trade update to Kafka for UI consumption
                    try {
                        String tradeJson = mapper.writeValueAsString(trade);
                        // kafkaTemplate.send("trade-updates", tradeJson);
                        pricePublisher.updateLatestPrice(tradeJson);
                    } catch (Exception e) {
                        System.err.println("Error publishing trade update: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Trade createTrade(String ticker, double price, long ts){
        Trade trade = new Trade();
        trade.setTicker(ticker);
        trade.setPrice(price);
        trade.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneOffset.UTC));
        return trade;
    }
}
// This service processes incoming market data messages, extracts trade information