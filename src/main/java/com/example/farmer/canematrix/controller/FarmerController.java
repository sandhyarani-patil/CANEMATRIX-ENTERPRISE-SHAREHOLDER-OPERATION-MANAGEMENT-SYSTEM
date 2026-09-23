package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.request.FarmerRequest;
import com.example.farmer.canematrix.dto.response.FarmerResponse;
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

    // ==========================================
    // 2. BANK DETAILS APIs
    // ==========================================

    @PostMapping("/{farmerId}/bank")
    public ResponseEntity<FarmerBankDetail> addBankDetail(
            @PathVariable Long farmerId,
            @Valid @RequestBody FarmerBankDetail bankDetail
    ) {
        return new ResponseEntity<>(bankService.addBankDetail(farmerId, bankDetail), HttpStatus.CREATED);
    }

    @GetMapping("/{farmerId}/bank")
    public ResponseEntity<FarmerBankDetail> getBankDetail(@PathVariable Long farmerId) {
        return ResponseEntity.ok(bankService.getBankDetailByFarmerId(farmerId));
    }

    @PutMapping("/bank/{bankId}")
    public ResponseEntity<FarmerBankDetail> updateBankDetail(
            @PathVariable Long bankId,
            @Valid @RequestBody FarmerBankDetail bankDetail
    ) {
        return ResponseEntity.ok(bankService.updateBankDetail(bankId, bankDetail));
    }

    @PatchMapping("/bank/{bankId}")
    public ResponseEntity<FarmerBankDetail> partialUpdateBankDetail(
            @PathVariable Long bankId,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(bankService.partialUpdateBankDetail(bankId, updates));
    }

    // ==========================================
    // 3. FARM DETAILS APIs
    // ==========================================

    @PostMapping("/{farmerId}/farms")
    public ResponseEntity<FarmerFarmDetail> addFarmDetail(
            @PathVariable Long farmerId,
            @Valid @RequestBody FarmerFarmDetail farmDetail
    ) {
        return new ResponseEntity<>(farmService.addFarmDetail(farmerId, farmDetail), HttpStatus.CREATED);
    }

    @GetMapping("/{farmerId}/farms")
    public ResponseEntity<List<FarmerFarmDetail>> getFarmsByFarmerId(@PathVariable Long farmerId) {
        return ResponseEntity.ok(farmService.getFarmsByFarmerId(farmerId));
    }

    @PutMapping("/farms/{farmId}")
    public ResponseEntity<FarmerFarmDetail> updateFarmDetail(
            @PathVariable Long farmId,
            @Valid @RequestBody FarmerFarmDetail farmDetail
    ) {
        return ResponseEntity.ok(farmService.updateFarmDetail(farmId, farmDetail));
    }

    @DeleteMapping("/farms/{farmId}")
    public ResponseEntity<String> deleteFarmDetail(@PathVariable Long farmId) {
        farmService.deleteFarmDetail(farmId);
        return ResponseEntity.ok("Farm record deleted successfully with ID: " + farmId);
    }

    // ==========================================
    // 4. NOMINEE DETAILS APIs
    // ==========================================

    @PostMapping("/{farmerId}/nominee")
    public ResponseEntity<FarmerNominee> addNominee(
            @PathVariable Long farmerId,
            @Valid @RequestBody FarmerNominee nominee
    ) {
        return new ResponseEntity<>(nomineeService.addNominee(farmerId, nominee), HttpStatus.CREATED);
    }

    @GetMapping("/{farmerId}/nominee")
    public ResponseEntity<FarmerNominee> getNominee(@PathVariable Long farmerId) {
        return ResponseEntity.ok(nomineeService.getNomineeByFarmerId(farmerId));
    }

    @PatchMapping("/nominee/{nomineeId}")
    public ResponseEntity<FarmerNominee> partialUpdateNominee(
            @PathVariable Long nomineeId,
            @RequestBody Map<String, Object> updates
    ) {
        return ResponseEntity.ok(nomineeService.partialUpdateNominee(nomineeId, updates));
    }
}