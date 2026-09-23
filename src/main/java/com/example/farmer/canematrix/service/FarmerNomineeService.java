package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.entity.FarmerNominee;
import java.util.Map;

public interface FarmerNomineeService {
    FarmerNominee addNominee(Long farmerId, FarmerNominee nominee);
    FarmerNominee getNomineeByFarmerId(Long farmerId);
    FarmerNominee partialUpdateNominee(Long nomineeId, Map<String, Object> updates);
}