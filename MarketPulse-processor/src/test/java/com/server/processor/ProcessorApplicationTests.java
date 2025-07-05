package com.server.processor;

import com.server.processor.service.TradeService;
import com.server.processor.model.Trade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProcessorApplicationTests {

    @Autowired
    private TradeService tradeService;

    @Test
    void contextLoads() {
        assertThat(tradeService).isNotNull();
    }

    @Test
    void testCreateTrade() {
        String ticker = "AAPL";
        double price = 150.0;
        long timestamp = 1672531200000L;

        Trade trade = TradeService.createTrade(ticker, price, timestamp);

        assertThat(trade).isNotNull();
        assertThat(trade.getTicker()).isEqualTo(ticker);
        assertThat(trade.getPrice()).isEqualTo(price);

        LocalDateTime expectedTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
        assertThat(trade.getTimestamp()).isEqualTo(expectedTimestamp);
    }
}
