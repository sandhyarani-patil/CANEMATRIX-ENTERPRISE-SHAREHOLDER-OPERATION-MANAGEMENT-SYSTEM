package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.service.SugarFactoryRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factory-rates")
public class SugarFactoryRateController {

    @Autowired
    private SugarFactoryRateService service;

    // Admin कडून नवीन दर किंवा कारखान्याची माहिती सेट करण्यासाठी
    @PostMapping
    public ResponseEntity<SugarFactoryRate> createOrUpdateRate(@RequestBody SugarFactoryRate rate) {
        return ResponseEntity.ok(service.saveOrUpdateRate(rate));
    }

    // सध्याचे चालू असणारे लेटेस्ट दर पाहण्यासाठी
    @GetMapping("/latest")
    public ResponseEntity<SugarFactoryRate> getLatestRate() {
        SugarFactoryRate latestRate = service.getLatestRate();
        if (latestRate != null) {
            return ResponseEntity.ok(latestRate);
        }
        return ResponseEntity.notFound().build();
    }
    // सर्व रेकॉर्ड्स पाहण्यासाठी
    @GetMapping
    public ResponseEntity<List<SugarFactoryRate>> getAllRates() {
        return ResponseEntity.ok(service.getAllRates());
    }
}