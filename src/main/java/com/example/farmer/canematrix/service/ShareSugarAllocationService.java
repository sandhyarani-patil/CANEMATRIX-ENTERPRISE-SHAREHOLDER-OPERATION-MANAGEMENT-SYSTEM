package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.dto.ShareSugarAllocationDto;
import com.example.farmer.canematrix.dto.SugarLiftReceiptDto;
import com.example.farmer.canematrix.entity.SugarLiftHistory; // हे इम्पोर्ट करायला विसरू नका
import java.util.List;

public interface ShareSugarAllocationService {
    ShareSugarAllocationDto createShareSugarAllocation(ShareSugarAllocationDto requestDto);
    List<ShareSugarAllocationDto> getAllShareSugarAllocations();
    ShareSugarAllocationDto getShareSugarAllocationByFarmerCode(String farmerCode);

    List<SugarLiftHistory> getFarmerLiftHistory(String farmerCode); // <<-- ही ओळ ॲड करा
    void deleteShareSugarAllocation(Long id);
    SugarLiftReceiptDto liftSugar(String farmerCode, double quantityToLift);

    // <<-- ही नवीन पीडीएफ मेथड इथे इंटरफेसमध्ये ॲड करा -->>
    byte[] generateLiftReceiptPdf(SugarLiftReceiptDto receipt);
}