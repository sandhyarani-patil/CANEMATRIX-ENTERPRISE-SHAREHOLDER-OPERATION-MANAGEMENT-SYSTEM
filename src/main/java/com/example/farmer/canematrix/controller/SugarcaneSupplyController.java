package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.FactorySummaryDTO;
import com.example.farmer.canematrix.dto.FarmerSupplySummaryDTO;
import com.example.farmer.canematrix.dto.VehicleSummaryDTO; // 👈 ॲड केले
import com.example.farmer.canematrix.entity.SugarcaneSupply;
import com.example.farmer.canematrix.service.SugarcaneSupplyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sugarcane-supply")

public class SugarcaneSupplyController {

    @Autowired
    private SugarcaneSupplyService supplyService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CLERK') or hasRole('ADMIN')")
    public ResponseEntity<SugarcaneSupply> addSupplyEntry(@RequestBody SugarcaneSupply supplyRequest) {

        SugarcaneSupply savedSupply = supplyService.addSupplyEntry(
                supplyRequest.getFarmerCode(),
                supplyRequest.getFarmCode(),
                supplyRequest.getTonnes(),
                supplyRequest.getTractorNumber(),
                supplyRequest.getDriverName(),
                supplyRequest.getPlantingDate()
        );

        return new ResponseEntity<>(savedSupply, HttpStatus.CREATED);
    }

    @GetMapping("/summary/{farmerCode}")
    public ResponseEntity<FarmerSupplySummaryDTO> getFarmerSupplySummary(@PathVariable String farmerCode) {
        FarmerSupplySummaryDTO summary = supplyService.getFarmerSupplySummary(farmerCode);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SugarcaneSupply>> getAllSupplies() {
        List<SugarcaneSupply> supplies = supplyService.getAllSupplies();
        return ResponseEntity.ok(supplies);
    }

    @GetMapping("/receipt/{supplyId}")
    public ResponseEntity<byte[]> downloadReceiptPdf(@PathVariable Long supplyId) {
        byte[] pdfBytes = supplyService.generateSupplyReceiptPdf(supplyId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Sugarcane_Receipt_" + supplyId + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SugarcaneSupply>> getSuppliesByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<SugarcaneSupply> filteredSupplies = supplyService.getSuppliesByDateRange(startDate, endDate);
        return ResponseEntity.ok(filteredSupplies);
    }

    // 👇 नवीन: वाहन समरी (Vehicle Summary) तपासण्यासाठी GET API
    @GetMapping("/vehicle-summary")
    public ResponseEntity<VehicleSummaryDTO> getVehicleSummary(
            @RequestParam("tractorNumber") String tractorNumber,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        VehicleSummaryDTO summary = supplyService.getVehicleSummary(tractorNumber, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    // 👇 नवीन: फॅक्टरीचा दोन तारखांमधील किंवा महिन्याचा समरी रिपोर्ट
    @GetMapping("/factory-summary")
    public ResponseEntity<FactorySummaryDTO> getFactorySummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        FactorySummaryDTO summary = supplyService.getFactorySummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }
}