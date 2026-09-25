package com.example.farmer.canematrix.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TonnesSugarReceiptDto {

    private Long liftHistoryId;


    private String farmerCode;
    private String farmerName;
    private Double totalTonnes;
    private BigDecimal ratePerKg;
    private Double tonnesSugarKg;          // एकूण मिळणारी साखर
    private Double suppliedSugarKg;        // आतापर्यंत नेलेली एकूण साखर
    private Double remainingSugarKg;       // शिल्लक साखर
    private Double previousRemSugar;       // मागची शिल्लक साखर
    private BigDecimal amountOfTonnesSugar;// एकूण साखरेची किंमत
    private Double currentLiftedKg;        // या फेरीत नेलेली साखर
    private BigDecimal currentBillAmount;  // या फेरीचे बिल
    private LocalDate liftDate;
    private String status;
}