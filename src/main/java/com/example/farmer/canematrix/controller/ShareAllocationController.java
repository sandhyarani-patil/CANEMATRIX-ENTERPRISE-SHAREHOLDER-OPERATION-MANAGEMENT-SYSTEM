package com.example.farmer.canematrix.controller;


import com.example.farmer.canematrix.dto.ShareAllocationDto;
import com.example.farmer.canematrix.service.ShareAllocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share-allocations")
public class ShareAllocationController {

    @Autowired
    private ShareAllocationService service;

    // 1. Create Share Allocation
    @PostMapping
    public ResponseEntity<ShareAllocationDto> createShareAllocation(@Valid @RequestBody ShareAllocationDto dto) {
        ShareAllocationDto createdDto = service.createShareAllocation(dto);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    // 2. Get All Share Allocations
    @GetMapping
    public ResponseEntity<List<ShareAllocationDto>> getAllShareAllocations() {
        return ResponseEntity.ok(service.getAllShareAllocations());
    }

    // 3. Get Share Allocation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ShareAllocationDto> getShareAllocationById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getShareAllocationById(id));
    }

    // 4. Get Share Allocations by Farmer Code
    @GetMapping("/farmer/{farmerCode}")
    public ResponseEntity<List<ShareAllocationDto>> getShareAllocationsByFarmerCode(@PathVariable String farmerCode) {
        return ResponseEntity.ok(service.getShareAllocationsByFarmerCode(farmerCode));
    }

    // 5. Update Share Allocation
    @PutMapping("/{id}")
    public ResponseEntity<ShareAllocationDto> updateShareAllocation(
            @PathVariable Long id,
            @Valid @RequestBody ShareAllocationDto dto) {
        return ResponseEntity.ok(service.updateShareAllocation(id, dto));
    }

    // 6. Delete Share Allocation
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShareAllocation(@PathVariable Long id) {
        service.deleteShareAllocation(id);
        return ResponseEntity.ok("Share Allocation deleted successfully with ID: " + id);
    }
}