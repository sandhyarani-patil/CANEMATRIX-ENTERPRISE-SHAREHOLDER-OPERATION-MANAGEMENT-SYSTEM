package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.entity.ShareTransfer;
import com.example.farmer.canematrix.service.impl.ShareTransferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/share-transfer")

public class ShareTransferController {

    @Autowired
    private ShareTransferServiceImpl service;

    // 1. शेअर ट्रान्सफरसाठी अर्ज करणे (फक्त क्लार्क किंवा ॲडमिन करू शकतील)
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLERK') or hasRole('ADMIN')")
    public ResponseEntity<ShareTransfer> createTransfer(
            @RequestParam("farmerCode") String farmerCode,
            @RequestParam("transferReason") String transferReason,
            @RequestParam(value = "document", required = false) MultipartFile document) {

        ShareTransfer transfer = service.createTransferRequest(farmerCode, transferReason, document);
        return ResponseEntity.ok(transfer);
    }

    // 2. अर्ज Approve किंवा Reject करणे (फक्त ॲडमिन/मॅनेजर करू शकेल)
    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShareTransfer> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") ShareTransfer.TransferStatus status) { // PENDING, APPROVED, REJECTED

        ShareTransfer updated = service.updateTransferStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    // 3. सर्व ट्रान्सफर अर्जांची लिस्ट पाहणे
    @GetMapping("/all")
    @PreAuthorize("hasRole('CLERK') or hasRole('ADMIN')")
    public ResponseEntity<List<ShareTransfer>> getAllTransfers() {
        return ResponseEntity.ok(service.getAllTransfers());
    }

    // 4. शेतकरी कोडनुसार ट्रान्सफर हिस्ट्री पाहणे
    @GetMapping("/farmer/{farmerCode}")
    public ResponseEntity<List<ShareTransfer>> getTransfersByFarmer(@PathVariable String farmerCode) {
        return ResponseEntity.ok(service.getTransfersByFarmer(farmerCode));
    }

    // शेअर ट्रान्सफर मंजूर झाल्यानंतर PDF पावती डाऊनलोड करणे
    @GetMapping("/receipt/pdf/{id}")
    public ResponseEntity<byte[]> downloadTransferReceiptPdf(@PathVariable Long id) {

        byte[] pdfBytes = service.generateTransferReceiptPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Share_Transfer_Certificate_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}