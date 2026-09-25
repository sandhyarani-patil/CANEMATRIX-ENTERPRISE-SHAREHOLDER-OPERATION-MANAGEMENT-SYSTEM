package com.example.farmer.canematrix.controller;


import com.example.farmer.canematrix.dto.FarmerRequest;
import com.example.farmer.canematrix.dto.FarmerResponse;
import com.example.farmer.canematrix.entity.FarmerBankDetail;
import com.example.farmer.canematrix.entity.FarmerFarmDetail;
import com.example.farmer.canematrix.entity.FarmerNominee;
import com.example.farmer.canematrix.service.FarmerBankDetailService;
import com.example.farmer.canematrix.service.FarmerFarmDetailService;
import com.example.farmer.canematrix.service.FarmerNomineeService;
import com.example.farmer.canematrix.service.FarmerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "http://localhost:5173")
public class FarmerController {

    private final FarmerService farmerService;
    private final FarmerBankDetailService bankService;
    private final FarmerFarmDetailService farmService;
    private final FarmerNomineeService nomineeService;

    // Constructor Injection
    public FarmerController(
            FarmerService farmerService,
            FarmerBankDetailService bankService,
            FarmerFarmDetailService farmService,
            FarmerNomineeService nomineeService
    ) {
        this.farmerService = farmerService;
        this.bankService = bankService;
        this.farmService = farmService;
        this.nomineeService = nomineeService;
    }

    // ==========================================
    // 1. FARMER MAIN APIs
    // ==========================================

    @PostMapping
    public ResponseEntity<FarmerResponse> createFarmer(@Valid @RequestBody FarmerRequest request) {
        FarmerResponse response = farmerService.createFarmer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FarmerResponse>> getAllFarmers() {
        return ResponseEntity.ok(farmerService.getAllFarmers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmerResponse> getFarmerById(@PathVariable Long id) {
        return ResponseEntity.ok(farmerService.getFarmerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmerResponse> updateFarmer(
            @PathVariable Long id,
            @Valid @RequestBody FarmerRequest request
    ) {
        return ResponseEntity.ok(farmerService.updateFarmer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFarmer(@PathVariable Long id) {
        farmerService.deleteFarmer(id);
        return ResponseEntity.ok("Farmer record deleted successfully with ID: " + id);
    }




}