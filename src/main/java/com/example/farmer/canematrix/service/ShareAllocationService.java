package com.example.farmer.canematrix.service;


import com.example.farmer.canematrix.dto.ShareAllocationDto;

import java.util.List;

public interface ShareAllocationService {

    ShareAllocationDto createShareAllocation(ShareAllocationDto dto);

    ShareAllocationDto getShareAllocationById(Long id);

    List<ShareAllocationDto> getAllShareAllocations();

    List<ShareAllocationDto> getShareAllocationsByFarmerCode(String farmerCode);

    ShareAllocationDto updateShareAllocation(Long id, ShareAllocationDto dto);

    void deleteShareAllocation(Long id);
}