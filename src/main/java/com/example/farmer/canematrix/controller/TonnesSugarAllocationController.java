package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.TonnesSugarReceiptDto;
import com.example.farmer.canematrix.entity.TonnesSugarAllocation;
import com.example.farmer.canematrix.entity.TonnesSugarHistory;
import com.example.farmer.canematrix.service.impl.TonnesSugarAllocationServiceImpl;
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
    private TonnesSugarAllocationServiceImpl service;

    // 1. नवीन ॲलोकेशन तयार करणे (नाव आणि रेट बॅकएंड स्वतः फेच करेल)
    @PostMapping("/create")
    public ResponseEntity<TonnesSugarAllocation> createAllocation(
            @RequestParam String farmerCode,
            @RequestParam Double totalTonnes) {

        TonnesSugarAllocation allocation = service.createAllocation(farmerCode, totalTonnes);
        return ResponseEntity.ok(allocation);
    }

    // 2. साखर उचलणे आणि PDF पावती डाऊनलोड करणे
    @GetMapping("/lift/pdf")
    public ResponseEntity<byte[]> liftAndDownloadPdf(
            @RequestParam String farmerCode,
            @RequestParam double quantity) {

        TonnesSugarReceiptDto receipt = service.liftTonnesSugar(farmerCode, quantity);
        byte[] pdfBytes = service.generateTonnesReceiptPdf(receipt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Cane_Sugar_Receipt_" + farmerCode + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // 3. सर्व शेतकर्‍यांची ॲलोकेशन लिस्ट मिळवणे
    @GetMapping("/all")
    public ResponseEntity<List<TonnesSugarAllocation>> getAllAllocations() {
        List<TonnesSugarAllocation> list = service.getAllAllocations();
        return ResponseEntity.ok(list);
    }

    // 4. शेतकर्‍याचा कोड टाकून त्याची हिस्ट्री मिळवणे
    @GetMapping("/history")
    public ResponseEntity<List<TonnesSugarHistory>> getFarmerHistory(@RequestParam String farmerCode) {
        List<TonnesSugarHistory> historyList = service.getFarmerTonnesHistory(farmerCode);
        return ResponseEntity.ok(historyList);
    }
}