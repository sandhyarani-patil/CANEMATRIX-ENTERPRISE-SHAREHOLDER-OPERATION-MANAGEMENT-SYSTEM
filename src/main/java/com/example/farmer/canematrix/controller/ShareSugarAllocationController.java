package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.ShareSugarAllocationDto;
import com.example.farmer.canematrix.service.ShareSugarAllocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share-sugar-allocation")
public class ShareSugarAllocationController {

    @Autowired
    private ShareSugarAllocationService service;

    // Create Share Sugar Allocation (Farmer code & Year takala ki sagla auto calculate hoil)
    @PostMapping
    public ResponseEntity<ShareSugarAllocationDto> createAllocation(@Valid @RequestBody ShareSugarAllocationDto dto) {
        ShareSugarAllocationDto created = service.createShareSugarAllocation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get All Allocations
    @GetMapping
    public ResponseEntity<List<ShareSugarAllocationDto>> getAllAllocations() {
        return ResponseEntity.ok(service.getAllShareSugarAllocations());
    }

    // Get By Farmer Code (Receipt / Bill view sathi)
    @GetMapping("/farmer/{farmerCode}")
    public ResponseEntity<ShareSugarAllocationDto> getAllocationByFarmerCode(@PathVariable String farmerCode) {
        return ResponseEntity.ok(service.getShareSugarAllocationByFarmerCode(farmerCode));
    }

    // Delete Allocation
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAllocation(@PathVariable Long id) {
        service.deleteShareSugarAllocation(id);
        return ResponseEntity.ok("Share sugar allocation deleted successfully with ID: " + id);
    }
}