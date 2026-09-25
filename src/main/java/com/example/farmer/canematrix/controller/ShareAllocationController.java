package com.example.farmer.canematrix.controller;

import com.example.farmer.canematrix.dto.ShareAllocationDto;
import com.example.farmer.canematrix.entity.Farmer;
import com.example.farmer.canematrix.entity.SugarFactoryRate;
import com.example.farmer.canematrix.exception.FarmerNotFoundException;
import com.example.farmer.canematrix.exception.ResourceNotFoundException;
import com.example.farmer.canematrix.repository.FarmerRepository;
import com.example.farmer.canematrix.repository.SugarFactoryRateRepository;
import com.example.farmer.canematrix.service.ShareAllocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/share-allocations")
public class ShareAllocationController {

    @Autowired
    private ShareAllocationService service;

    @Autowired
    private FarmerRepository farmerRepository; // 👈 नवीन ऍड केले

    @Autowired
    private SugarFactoryRateRepository sugarFactoryRateRepository; // 👈 नवीन ऍड केले

    // 👇 नवीन API: Farmer Code नुसार नाव, नॉमिनी आणि दर फेच करण्यासाठी
    @GetMapping("/farmer-details/{farmerCode}")
    public ResponseEntity<Map<String, Object>> getFarmerAndRateDetails(@PathVariable String farmerCode) {

        // १. शेतकरी शोधत आहे
        Farmer farmer = farmerRepository.findByFarmerCode(farmerCode)
                .orElseThrow(() -> new FarmerNotFoundException("Farmer not found with code: " + farmerCode));

        // २. रेट मास्टरमधील लेटेस्ट दर आणत आहे
        SugarFactoryRate latestRate = sugarFactoryRateRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Factory rates are not configured in Rate Master!"));

        // ३. डेटा मॅप करून पाठवत आहे
        Map<String, Object> response = new HashMap<>();
        response.put("farmerName", farmer.getFarmerName());
        response.put("nomineeName", farmer.getNominee() != null ? farmer.getNominee().getNomineeName() : "Not Available");
        response.put("ratePerKg", latestRate.getRateOfShareSugar());
        response.put("perMonthSugarKg", latestRate.getPerMonthShareSugar());
        response.put("sharePrice", latestRate.getSharePurchaseAmount());

        return ResponseEntity.ok(response);
    }

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