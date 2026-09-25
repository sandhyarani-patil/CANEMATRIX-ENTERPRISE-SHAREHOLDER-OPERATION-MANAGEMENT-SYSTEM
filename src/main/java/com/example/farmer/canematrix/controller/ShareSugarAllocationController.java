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

import java.security.Principal; // 👈 1. He import add karayche ahe!
import java.util.List;

@RestController
@RequestMapping("/api/share-sugar-allocation")
public class ShareSugarAllocationController {

    @Autowired
    private ShareSugarAllocationService allocationService;

    @PostMapping
    public ResponseEntity<ShareSugarAllocationDto> createAllocation(@Valid @RequestBody ShareSugarAllocationDto dto) {
        ShareSugarAllocationDto created = allocationService.createShareSugarAllocation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ShareSugarAllocationDto>> getAllAllocations() {
        List<ShareSugarAllocationDto> list = allocationService.getAllShareSugarAllocations();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/farmer/{farmerCode}")
    public ResponseEntity<ShareSugarAllocationDto> getAllocationByFarmerCode(@PathVariable String farmerCode) {
        ShareSugarAllocationDto dto = allocationService.getShareSugarAllocationByFarmerCode(farmerCode);
        return ResponseEntity.ok(dto);
    }

    // State badalnyasathi PUT request (फक्त इथेच साखर खाली होईल)
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAllocation(@PathVariable Long id) {
        allocationService.deleteShareSugarAllocation(id);
        return ResponseEntity.ok("Share sugar allocation deleted successfully!");
    }

    // Safe & Idempotent PDF download using historyId (फक्त वाचणे, डेटा बदलत नाही)
    @GetMapping("/lift/pdf/{historyId}")
    public ResponseEntity<byte[]> downloadLiftReceiptPdf(@PathVariable Long historyId) {
        byte[] pdfBytes = allocationService.generateLiftReceiptPdfByHistoryId(historyId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Sugar_Receipt_" + historyId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    // 🌾 Farmer swatahi fukt tyachi sugar status baghu shakel
    @GetMapping("/my-sugar-status")
    public ResponseEntity<ShareSugarAllocationDto> getMySugarStatus(Principal principal) {
        String loggedInFarmerCode = principal.getName();
        ShareSugarAllocationDto dto = allocationService.getShareSugarAllocationByFarmerCode(loggedInFarmerCode);
        return ResponseEntity.ok(dto);
    }

    // 📜 Farmer swatahi tyachi sugar lift history baghu shakel
    @GetMapping("/my-history")
    public ResponseEntity<List<SugarLiftHistory>> getMyLiftHistory(Principal principal) {
        String loggedInFarmerCode = principal.getName();
        return ResponseEntity.ok(allocationService.getFarmerLiftHistory(loggedInFarmerCode));
    }
}