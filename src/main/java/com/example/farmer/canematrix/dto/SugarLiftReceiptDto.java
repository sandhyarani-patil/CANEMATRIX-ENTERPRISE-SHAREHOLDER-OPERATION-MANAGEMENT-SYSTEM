package com.example.farmer.canematrix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugarLiftReceiptDto {

    private Long liftHistoryId;
    private String farmerCode;
    private String farmerName;
    private String allocationYear;

    private double currentLiftedKg;     // या फेरीत नेलेली साखर
    private BigDecimal ratePerKg;       // दर प्रति किलो
    private BigDecimal currentBillAmount; // या खेपेचे बिल (currentLiftedKg * ratePerKg)

    private double totalLiftedSoFar;    // आतापर्यंतची एकूण नेलेली साखर
    private double remainingSugarKg;    // उरलेली शिल्लक साखर
    private LocalDate liftDate;         // साखर नेल्याची तारीख
    private String status;

    // ACTIVE / COMPLETED




}