package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.entity.FarmerFarmDetail;
import java.util.List;

public interface FarmerFarmDetailService {
    FarmerFarmDetail addFarmDetail(Long farmerId, FarmerFarmDetail farmDetail);
    List<FarmerFarmDetail> getFarmsByFarmerId(Long farmerId);
    FarmerFarmDetail updateFarmDetail(Long farmId, FarmerFarmDetail updatedDetail);
    void deleteFarmDetail(Long farmId);
}