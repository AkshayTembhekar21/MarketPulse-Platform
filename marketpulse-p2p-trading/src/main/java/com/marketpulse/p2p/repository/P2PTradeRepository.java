package com.marketpulse.p2p.repository;

import com.marketpulse.p2p.model.P2PTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface P2PTradeRepository extends JpaRepository<P2PTrade, Long> {
    List<P2PTrade> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
    List<P2PTrade> findByReceiverIdAndStatus(Long receiverId, String status);
    List<P2PTrade> findBySenderIdOrReceiverIdAndStatusNot(Long senderId, Long receiverId, String status);
} 