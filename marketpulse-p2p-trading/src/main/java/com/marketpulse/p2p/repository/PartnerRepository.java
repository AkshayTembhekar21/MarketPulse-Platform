package com.marketpulse.p2p.repository;

import com.marketpulse.p2p.model.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    List<Partner> findByUserId(Long userId);
    List<Partner> findByPartnerId(Long partnerId);
} 