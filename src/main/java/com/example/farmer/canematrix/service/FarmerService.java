package com.example.farmer.canematrix.service;


import com.example.farmer.canematrix.dto.FarmerRequest;
import com.example.farmer.canematrix.dto.FarmerResponse;

import java.util.List;

public interface FarmerService {

    FarmerResponse createFarmer(FarmerRequest request);

    List<FarmerResponse> getAllFarmers();

    FarmerResponse getFarmerById(Long id);

    FarmerResponse updateFarmer(Long id, FarmerRequest request);

    void deleteFarmer(Long id);
}