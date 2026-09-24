package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.ShareSugarAllocationDto;
import com.example.farmer.canematrix.dto.SugarLiftReceiptDto;
import com.example.farmer.canematrix.entity.SugarLiftHistory;
import com.example.farmer.canematrix.service.ShareSugarAllocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share-sugar-allocation")
public class ShareSugarAllocationController {

    @Autowired
    private ShareSugarAllocationService allocationService;

    // नवीन ॲलोकेशन तयार करणे
    @PostMapping
    public ResponseEntity<ShareSugarAllocationDto> createAllocation(@Valid @RequestBody ShareSugarAllocationDto dto) {
        ShareSugarAllocationDto created = allocationService.createShareSugarAllocation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // सर्व ॲलोकेशन पाहणे
    @GetMapping
    public ResponseEntity<List<ShareSugarAllocationDto>> getAllAllocations() {
        List<ShareSugarAllocationDto> list = allocationService.getAllShareSugarAllocations();
        return ResponseEntity.ok(list);
    }

    // शेतकरी कोडवरून ॲलोकेशन शोधणे
    @GetMapping("/farmer/{farmerCode}")
    public ResponseEntity<ShareSugarAllocationDto> getAllocationByFarmerCode(@PathVariable String farmerCode) {
        ShareSugarAllocationDto dto = allocationService.getShareSugarAllocationByFarmerCode(farmerCode);
        return ResponseEntity.ok(dto);
    }

    // साखर उचलणे आणि पावती (Receipt) जनरेट करणे
    @PutMapping("/lift")
    public ResponseEntity<SugarLiftReceiptDto> liftSugar(
            @RequestParam String farmerCode,
            @RequestParam double quantity) {
        SugarLiftReceiptDto receipt = allocationService.liftSugar(farmerCode, quantity);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping("/history/{farmerCode}")
    public ResponseEntity<List<SugarLiftHistory>> getLiftHistory(@PathVariable String farmerCode) {
        return ResponseEntity.ok(allocationService.getFarmerLiftHistory(farmerCode));
    }

    @GetMapping("/lift/pdf")
    public ResponseEntity<byte[]> downloadLiftReceiptPdf(
            @RequestParam String farmerCode,
            @RequestParam double quantity) {

        // 1. आधी साखर उचलून पावतीचा डेटा मिळवणे
        SugarLiftReceiptDto receipt = allocationService.liftSugar(farmerCode, quantity);

        // 2. त्या डेटावरून PDF बाईट्स जनरेट करणे
        byte[] pdfBytes = allocationService.generateLiftReceiptPdf(receipt);

        // 3. ब्राऊझर किंवा पोस्टमनला PDF फाईल डाऊनलोडसाठी पाठवणे
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Sugar_Receipt_" + farmerCode + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // रेकॉर्ड डिलीट करणे
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAllocation(@PathVariable Long id) {
        allocationService.deleteShareSugarAllocation(id);
        return ResponseEntity.ok("Share sugar allocation deleted successfully!");
    }
}