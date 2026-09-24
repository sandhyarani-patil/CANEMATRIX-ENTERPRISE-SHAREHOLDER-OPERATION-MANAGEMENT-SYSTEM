package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.FarmerSupplySummaryDTO;
import com.example.farmer.canematrix.entity.SugarcaneSupply;
import com.example.farmer.canematrix.service.SugarcaneSupplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sugarcane-supply")
@CrossOrigin(origins = "*") // फ्रंटएंड कनेक्ट करण्यासाठी CORS इनेबल केला आहे
public class SugarcaneSupplyController {

    @Autowired
    private SugarcaneSupplyService supplyService;

    // १. नवीन ऊस पुरवठा नोंदवणे (POST with JSON Body)
    @PostMapping("/add")
    public ResponseEntity<SugarcaneSupply> addSupplyEntry(@RequestBody SugarcaneSupply supplyRequest) {

        SugarcaneSupply savedSupply = supplyService.addSupplyEntry(
                supplyRequest.getFarmerCode(),
                supplyRequest.getTonnes(),
                supplyRequest.getTractorNumber(),
                supplyRequest.getDriverName()
        );

        return new ResponseEntity<>(savedSupply, HttpStatus.CREATED);
    }

    // २. विशिष्ट शेतकऱ्याची सर्व हिस्ट्री आणि समरी मिळवणे (GET)
    @GetMapping("/summary/{farmerCode}")
    public ResponseEntity<FarmerSupplySummaryDTO> getFarmerSupplySummary(@PathVariable String farmerCode) {
        FarmerSupplySummaryDTO summary = supplyService.getFarmerSupplySummary(farmerCode);
        return ResponseEntity.ok(summary);
    }

    // ३. फॅक्टरीतील सर्व शेतकऱ्यांचा पुरवठा पाहणे (GET)
    @GetMapping("/all")
    public ResponseEntity<List<SugarcaneSupply>> getAllSupplies() {
        List<SugarcaneSupply> supplies = supplyService.getAllSupplies();
        return ResponseEntity.ok(supplies);
    }

    // ४. विशिष्ट खेपेची PDF वजन पावती डाऊनलोड करणे (GET)
    @GetMapping("/receipt/{supplyId}")
    public ResponseEntity<byte[]> downloadReceiptPdf(@PathVariable Long supplyId) {
        byte[] pdfBytes = supplyService.generateSupplyReceiptPdf(supplyId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Sugarcane_Receipt_" + supplyId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // ५. तारखेनुसार पुरवठा फिल्टर करणे (GET)
    // URL: http://localhost:8080/api/sugarcane-supply/filter?startDate=2026-09-01&endDate=2026-09-23
    @GetMapping("/filter")
    public ResponseEntity<List<SugarcaneSupply>> getSuppliesByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // 👉 इथे आधी sugarcaneSupplyService होतं, ते आपण 'supplyService' केलं आहे
        List<SugarcaneSupply> filteredSupplies = supplyService.getSuppliesByDateRange(startDate, endDate);
        return ResponseEntity.ok(filteredSupplies);
    }
}