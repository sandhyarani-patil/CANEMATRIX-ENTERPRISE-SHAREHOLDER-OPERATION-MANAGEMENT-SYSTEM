package com.example.farmer.canematrix.service;

import com.example.farmer.canematrix.dto.FarmerSupplySummaryDTO;
import com.example.farmer.canematrix.entity.SugarcaneSupply;

import java.time.LocalDate;
import java.util.List;

public interface SugarcaneSupplyService {

    // नवीन ऊस पुरवठा नोंदवणे
    SugarcaneSupply addSupplyEntry(String farmerCode, double tonnes, String tractorNumber, String driverName);

    // शेतकऱ्याची सर्व हिस्ट्री आणि समरी पाहणे
    FarmerSupplySummaryDTO getFarmerSupplySummary(String farmerCode);

    // सर्व पुरवठा पाहणे
    List<SugarcaneSupply> getAllSupplies();

    // PDF पावती जनरेट करणे
    byte[] generateSupplyReceiptPdf(Long supplyId);

    List<SugarcaneSupply> getSuppliesByDateRange(LocalDate startDate, LocalDate endDate);
}