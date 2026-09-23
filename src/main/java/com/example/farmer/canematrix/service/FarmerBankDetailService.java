package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.entity.FarmerBankDetail;
import java.util.Map;

public interface FarmerBankDetailService {
    FarmerBankDetail addBankDetail(Long farmerId, FarmerBankDetail bankDetail);
    FarmerBankDetail getBankDetailByFarmerId(Long farmerId);
    FarmerBankDetail updateBankDetail(Long bankId, FarmerBankDetail updatedDetail);
    FarmerBankDetail partialUpdateBankDetail(Long bankId, Map<String, Object> updates);
}