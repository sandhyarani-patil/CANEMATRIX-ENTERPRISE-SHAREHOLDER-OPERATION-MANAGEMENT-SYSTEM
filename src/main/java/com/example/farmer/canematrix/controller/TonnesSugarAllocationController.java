package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.TonnesSugarReceiptDto; // 👈 हे इम्पोर्ट असणे गरजेचे आहे
import com.example.farmer.canematrix.entity.TonnesSugarAllocation;
import com.example.farmer.canematrix.entity.TonnesSugarHistory;
import com.example.farmer.canematrix.service.TonnesSugarAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tonnes-sugar")
public class TonnesSugarAllocationController {

    @Autowired
    private TonnesSugarAllocationService service;

    @PostMapping("/create")
    public ResponseEntity<TonnesSugarAllocation> createAllocation(
            @RequestParam String farmerCode,
            @RequestParam Double totalTonnes) {

        TonnesSugarAllocation allocation = service.createAllocation(farmerCode, totalTonnes);
        return ResponseEntity.ok(allocation);
    }

    // 🌟 ही नवीन 'lift' मेथड इथे ऍड केली आहे जी फ्रंटएंडच्या API कॉलला रेस्पॉंड करेल
    @PostMapping("/lift")
    public ResponseEntity<TonnesSugarReceiptDto> liftTonnesSugar(
            @RequestParam String farmerCode,
            @RequestParam Double quantityLifted,
            @RequestParam String liftDate) {

        TonnesSugarReceiptDto receipt = service.liftTonnesSugar(farmerCode, quantityLifted);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TonnesSugarAllocation>> getAllAllocations() {
        List<TonnesSugarAllocation> list = service.getAllAllocations();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TonnesSugarHistory>> getFarmerHistory(@RequestParam String farmerCode) {
        List<TonnesSugarHistory> historyList = service.getFarmerTonnesHistory(farmerCode);
        return ResponseEntity.ok(historyList);
    }

    // Safe & Idempotent PDF download using historyId
    @GetMapping("/lift/pdf/{historyId}")
    public ResponseEntity<byte[]> downloadLiftReceiptPdf(@PathVariable Long historyId) {
        byte[] pdfBytes = service.generateTonnesReceiptPdfByHistoryId(historyId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Cane_Sugar_Receipt_" + historyId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}