package com.example.farmer.canematrix.dto;

import com.example.farmer.canematrix.entity.SugarcaneSupply;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class FarmerSupplySummaryDTO {
    private String farmerCode;
    private String farmerName;
    private int totalTrips;         // एकूण फेऱ्या
    private double totalTonnes;     // एकूण टन ऊस
    private double grandTotalAmount;// एकूण मिळणारी रक्कम (टोटल बिल)
    private List<SugarcaneSupply> supplies; // सर्व मल्टिपल एन्ट्रीजची यादी
}