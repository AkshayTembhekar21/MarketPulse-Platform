package com.marketpulse.p2p.controller;

import com.marketpulse.p2p.model.P2PTrade;
import com.marketpulse.p2p.repository.P2PTradeRepository;
import com.marketpulse.p2p.kafka.P2PTradeKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trades")
public class P2PTradeController {
    @Autowired
    private P2PTradeRepository tradeRepository;

    @Autowired
    private P2PTradeKafkaProducer kafkaProducer;

    @GetMapping("/{userId}")
    public List<P2PTrade> getUserTrades(@PathVariable Long userId) {
        return tradeRepository.findBySenderIdOrReceiverId(userId, userId);
    }

    @PostMapping("/initiate")
    public P2PTrade initiateTrade(@RequestBody P2PTrade trade) {
        trade.setStatus("pending");
        trade.setCreatedAt(LocalDateTime.now());
        trade.setUpdatedAt(LocalDateTime.now());
        P2PTrade saved = tradeRepository.save(trade);
        kafkaProducer.sendTradeRequest(saved);
        return saved;
    }

    @PostMapping("/{id}/negotiate")
    public P2PTrade negotiate(@PathVariable Long id, @RequestBody String message) {
        Optional<P2PTrade> tradeOpt = tradeRepository.findById(id);
        if (tradeOpt.isPresent()) {
            P2PTrade trade = tradeOpt.get();
            trade.getNegotiationHistory().add(message);
            trade.setUpdatedAt(LocalDateTime.now());
            P2PTrade saved = tradeRepository.save(trade);
            kafkaProducer.sendTradeNegotiation(saved);
            return saved;
        }
        throw new RuntimeException("Trade not found");
    }

    @PostMapping("/{id}/accept")
    public P2PTrade acceptTrade(@PathVariable Long id) {
        Optional<P2PTrade> tradeOpt = tradeRepository.findById(id);
        if (tradeOpt.isPresent()) {
            P2PTrade trade = tradeOpt.get();
            trade.setStatus("agreed");
            trade.setUpdatedAt(LocalDateTime.now());
            P2PTrade saved = tradeRepository.save(trade);
            kafkaProducer.sendTradeConfirmation(saved);
            return saved;
        }
        throw new RuntimeException("Trade not found");
    }

    @PostMapping("/{id}/reject")
    public P2PTrade rejectTrade(@PathVariable Long id) {
        Optional<P2PTrade> tradeOpt = tradeRepository.findById(id);
        if (tradeOpt.isPresent()) {
            P2PTrade trade = tradeOpt.get();
            trade.setStatus("rejected");
            trade.setUpdatedAt(LocalDateTime.now());
            P2PTrade saved = tradeRepository.save(trade);
            kafkaProducer.sendTradeNegotiation(saved);
            return saved;
        }
        throw new RuntimeException("Trade not found");
    }
} 