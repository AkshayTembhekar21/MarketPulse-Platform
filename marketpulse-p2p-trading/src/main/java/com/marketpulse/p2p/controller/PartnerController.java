package com.marketpulse.p2p.controller;

import com.marketpulse.p2p.model.Partner;
import com.marketpulse.p2p.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/partners")
public class PartnerController {
    @Autowired
    private PartnerRepository partnerRepository;

    @GetMapping("/{userId}")
    public List<Partner> getPartners(@PathVariable Long userId) {
        return partnerRepository.findByUserId(userId);
    }

    @PostMapping
    public Partner addPartner(@RequestBody Partner partner) {
        return partnerRepository.save(partner);
    }
} 