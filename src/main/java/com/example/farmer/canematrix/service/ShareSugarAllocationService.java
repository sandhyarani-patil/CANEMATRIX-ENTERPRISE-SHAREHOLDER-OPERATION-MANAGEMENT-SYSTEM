package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.dto.ShareSugarAllocationDto;
import java.util.List;

public interface ShareSugarAllocationService {
    ShareSugarAllocationDto createShareSugarAllocation(ShareSugarAllocationDto requestDto);
    List<ShareSugarAllocationDto> getAllShareSugarAllocations();
    ShareSugarAllocationDto getShareSugarAllocationByFarmerCode(String farmerCode);
    void deleteShareSugarAllocation(Long id);
}