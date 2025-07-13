package com.marketpulse.p2p.service;

import com.marketpulse.p2p.model.P2PTrade;
import com.marketpulse.p2p.model.User;
import com.marketpulse.p2p.model.Partner;
import com.marketpulse.p2p.repository.P2PTradeRepository;
import com.marketpulse.p2p.repository.UserRepository;
import com.marketpulse.p2p.repository.PartnerRepository;
import com.marketpulse.p2p.kafka.P2PTradeKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class P2PTradeService {
    
    @Autowired
    private P2PTradeRepository tradeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PartnerRepository partnerRepository;
    
    @Autowired
    private P2PTradeKafkaProducer kafkaProducer;
    
    public List<P2PTrade> getUserTrades(Long userId) {
        validateUserExists(userId);
        return tradeRepository.findBySenderIdOrReceiverId(userId, userId);
    }
    
    public P2PTrade initiateTrade(P2PTrade trade) {
        validateTradeRequest(trade);
        
        trade.setStatus("pending");
        trade.setCreatedAt(LocalDateTime.now());
        trade.setUpdatedAt(LocalDateTime.now());
        trade.getNegotiationHistory().add("Trade initiated by user " + trade.getSenderId());
        
        P2PTrade saved = tradeRepository.save(trade);
        kafkaProducer.sendTradeRequest(saved);
        return saved;
    }
    
    public P2PTrade negotiate(Long tradeId, Long userId, String message) {
        P2PTrade trade = getTradeById(tradeId);
        validateUserCanNegotiate(trade, userId);
        
        String negotiationMessage = String.format("[%s] User %d: %s", 
            LocalDateTime.now(), userId, message);
        trade.getNegotiationHistory().add(negotiationMessage);
        trade.setUpdatedAt(LocalDateTime.now());
        
        P2PTrade saved = tradeRepository.save(trade);
        kafkaProducer.sendTradeNegotiation(saved);
        return saved;
    }
    
    public P2PTrade acceptTrade(Long tradeId, Long userId) {
        P2PTrade trade = getTradeById(tradeId);
        validateUserCanAccept(trade, userId);
        
        trade.setStatus("agreed");
        trade.setUpdatedAt(LocalDateTime.now());
        trade.getNegotiationHistory().add("Trade accepted by user " + userId);
        
        P2PTrade saved = tradeRepository.save(trade);
        kafkaProducer.sendTradeConfirmation(saved);
        return saved;
    }
    
    public P2PTrade rejectTrade(Long tradeId, Long userId) {
        P2PTrade trade = getTradeById(tradeId);
        validateUserCanReject(trade, userId);
        
        trade.setStatus("rejected");
        trade.setUpdatedAt(LocalDateTime.now());
        trade.getNegotiationHistory().add("Trade rejected by user " + userId);
        
        P2PTrade saved = tradeRepository.save(trade);
        kafkaProducer.sendTradeNegotiation(saved);
        return saved;
    }
    
    public P2PTrade getTradeById(Long tradeId) {
        return tradeRepository.findById(tradeId)
            .orElseThrow(() -> new RuntimeException("Trade not found with id: " + tradeId));
    }
    
    public List<P2PTrade> getPendingTrades(Long userId) {
        validateUserExists(userId);
        return tradeRepository.findByReceiverIdAndStatus(userId, "pending");
    }
    
    public List<P2PTrade> getActiveTrades(Long userId) {
        validateUserExists(userId);
        return tradeRepository.findBySenderIdOrReceiverIdAndStatusNot(userId, userId, "rejected");
    }
    
    private void validateTradeRequest(P2PTrade trade) {
        if (trade.getSenderId() == null || trade.getReceiverId() == null) {
            throw new RuntimeException("Sender and receiver IDs are required");
        }
        if (trade.getSenderId().equals(trade.getReceiverId())) {
            throw new RuntimeException("Sender and receiver cannot be the same user");
        }
        if (trade.getPrice() == null || trade.getPrice() <= 0) {
            throw new RuntimeException("Valid price is required");
        }
        validateUserExists(trade.getSenderId());
        validateUserExists(trade.getReceiverId());
    }
    
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
    
    private void validateUserCanNegotiate(P2PTrade trade, Long userId) {
        if (!trade.getSenderId().equals(userId) && !trade.getReceiverId().equals(userId)) {
            throw new RuntimeException("User not authorized to negotiate this trade");
        }
        if (!"pending".equals(trade.getStatus())) {
            throw new RuntimeException("Can only negotiate pending trades");
        }
    }
    
    private void validateUserCanAccept(P2PTrade trade, Long userId) {
        if (!trade.getReceiverId().equals(userId)) {
            throw new RuntimeException("Only receiver can accept the trade");
        }
        if (!"pending".equals(trade.getStatus())) {
            throw new RuntimeException("Can only accept pending trades");
        }
    }
    
    private void validateUserCanReject(P2PTrade trade, Long userId) {
        if (!trade.getSenderId().equals(userId) && !trade.getReceiverId().equals(userId)) {
            throw new RuntimeException("User not authorized to reject this trade");
        }
        if (!"pending".equals(trade.getStatus())) {
            throw new RuntimeException("Can only reject pending trades");
        }
    }
} 