package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.dto.FactorySummaryDTO;
import com.example.farmer.canematrix.dto.FarmerSupplySummaryDTO;
import com.example.farmer.canematrix.dto.VehicleSummaryDTO; // 👈 ॲड केले
import com.example.farmer.canematrix.entity.SugarcaneSupply;

import java.time.LocalDate;
import java.util.List;

public interface SugarcaneSupplyService {
    SugarcaneSupply addSupplyEntry(String farmerCode, String farmCode, double tonnes, String tractorNumber, String driverName, LocalDate plantingDate);
    FarmerSupplySummaryDTO getFarmerSupplySummary(String farmerCode);
    List<SugarcaneSupply> getAllSupplies();
    byte[] generateSupplyReceiptPdf(Long supplyId);
    List<SugarcaneSupply> getSuppliesByDateRange(LocalDate startDate, LocalDate endDate);

    // 👇 नवीन: वाहन समरी मेथड
    VehicleSummaryDTO getVehicleSummary(String tractorNumber, LocalDate startDate, LocalDate endDate);

    FactorySummaryDTO getFactorySummary(LocalDate startDate, LocalDate endDate);
}