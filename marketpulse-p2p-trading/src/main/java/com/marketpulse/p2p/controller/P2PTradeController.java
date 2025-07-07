package com.marketpulse.p2p.controller;

import com.marketpulse.p2p.model.P2PTrade;
import com.marketpulse.p2p.service.P2PTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/trades")
public class P2PTradeController {
    @Autowired
    private P2PTradeService tradeService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<P2PTrade>> getUserTrades(@PathVariable Long userId) {
        try {
            List<P2PTrade> trades = tradeService.getUserTrades(userId);
            return ResponseEntity.ok(trades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/pending")
    public ResponseEntity<List<P2PTrade>> getPendingTrades(@PathVariable Long userId) {
        try {
            List<P2PTrade> trades = tradeService.getPendingTrades(userId);
            return ResponseEntity.ok(trades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}/active")
    public ResponseEntity<List<P2PTrade>> getActiveTrades(@PathVariable Long userId) {
        try {
            List<P2PTrade> trades = tradeService.getActiveTrades(userId);
            return ResponseEntity.ok(trades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/initiate")
    public ResponseEntity<P2PTrade> initiateTrade(@RequestBody P2PTrade trade) {
        try {
            P2PTrade saved = tradeService.initiateTrade(trade);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/negotiate")
    public ResponseEntity<P2PTrade> negotiate(
            @PathVariable Long id, 
            @RequestParam Long userId,
            @RequestBody String message) {
        try {
            P2PTrade saved = tradeService.negotiate(id, userId, message);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<P2PTrade> acceptTrade(
            @PathVariable Long id, 
            @RequestParam Long userId) {
        try {
            P2PTrade saved = tradeService.acceptTrade(id, userId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<P2PTrade> rejectTrade(
            @PathVariable Long id, 
            @RequestParam Long userId) {
        try {
            P2PTrade saved = tradeService.rejectTrade(id, userId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/trade/{id}")
    public ResponseEntity<P2PTrade> getTradeById(@PathVariable Long id) {
        try {
            P2PTrade trade = tradeService.getTradeById(id);
            return ResponseEntity.ok(trade);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 